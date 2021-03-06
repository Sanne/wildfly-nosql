/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.extension.nosql.driver.neo4j.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

/**
 * SessionProxy
 *
 * @author Scott Marlow
 */
public class SessionProxy implements InvocationHandler {

    private volatile Object underlyingSession;
    private volatile Object underlyingTransaction;
    private final String profileName;

    private static final String SESSION_RESOURCE = "_nosqlSESSPROXY_";

    SessionProxy(Object session, String profileName) {
        this.underlyingSession = session;
        this.profileName = profileName;
    }

    static Object getSessionFromJTATransaction(TransactionSynchronizationRegistry transactionSynchronizationRegistry, String profileName) {
        return transactionSynchronizationRegistry.getResource(SESSION_RESOURCE + profileName);
    }

    static Object registerSessionWithJTATransaction(Object underlyingSession, TransactionManager transactionManager, TransactionSynchronizationRegistry transactionSynchronizationRegistry, String profileName, String jndiName) {
        SessionProxy sessionProxy = new SessionProxy(underlyingSession, profileName);
        Object sessionProxyInstance = Proxy.newProxyInstance(
                underlyingSession.getClass().getClassLoader(),
                underlyingSession.getClass().getInterfaces(),
                sessionProxy
                );

        TransactionControl transactionControl = sessionProxy.transactionControl();
        Neo4jXAResourceImpl resource = new Neo4jXAResourceImpl(transactionControl,jndiName, null, null);
        try {
            transactionManager.getTransaction().enlistResource(resource);
        } catch (RollbackException e) {
            throw new RuntimeException(e);
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
        transactionSynchronizationRegistry.putResource(SESSION_RESOURCE + profileName,sessionProxyInstance);
        return sessionProxyInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        if (method.getName().equals("beginTransaction")) {  /// Transaction beginTransaction();
            throw new RuntimeException("Incorrect use of JTA enlisted Session(" + profileName+ "), application should use JTA to control the transaction instead of Session.beginTransaction");
        }
        else if(method.getName().equals("close")) {
            // ignore call to close, as session/transaction will be auto-closed when JTA transaction ends.
            return null;
        } else {
            // we should have an underlying Neo4j transaction, redirect session invocations to transaction.
            // underlyingTransaction will only be non-null when we have an active JTA transaction.
            if (underlyingTransaction != null) {
                // lookup equivalent method on Transaction class (TODO: cache Transaction methods)
                method = underlyingTransaction.getClass().getMethod(method.getName(),method.getParameterTypes());
                result = method.invoke(underlyingTransaction, args);
            }
            else if(method.getName().equals("isOpen")) {
                // underlyingSession + underlyingTransaction must of been closed, handle isOpen by returning false
                return Boolean.FALSE;
            }
            else {
                // we are only proxying the session if there is an active JTA transaction, so we shouldn't reach the state of
                // no underlyingTransaction.  However, after the underlyingTransaction is closed,
                // no further calls should be made (other than close/isOpen).
                throw new RuntimeException("no underlying Neo4j transaction to invoke '" + method.getName()+"' with.");
            }
        }
        return result;
    }

    private TransactionControl transactionControl() {

        return new TransactionControl() {

            @Override
            public Object beginTransaction() {
                try {
                    underlyingTransaction = underlyingSession.getClass().getMethod("beginTransaction").invoke(underlyingSession, null);
                    return underlyingTransaction;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("could not begin transaction", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("could not begin transaction", e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("could not begin transaction", e);
                }
            }

            @Override
            public void success() {
                try {
                    underlyingTransaction.getClass().getMethod("success").invoke(underlyingTransaction, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("could not mark transaction as successful", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("could not mark transaction as successful", e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("could not mark transaction as successful", e);
                }
            }

            @Override
            public void failure() {
                try {
                    underlyingTransaction.getClass().getMethod("failure").invoke(underlyingTransaction, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("could not mark transaction as failed", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("could not mark transaction as failed", e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("could not mark transaction as failed", e);
                }
            }

            /**
             * close the underlying Neo4j Transaction and Neo4j Session when the JTA transaction ends.
             */
            @Override
            public void close() {
                try {
                    underlyingTransaction.getClass().getMethod("close").invoke(underlyingTransaction, null);
                    underlyingTransaction = null;
                    underlyingSession.getClass().getMethod("close").invoke(underlyingSession,null);
                    underlyingSession = null;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("could not close the transaction", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("could not close the transaction", e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("could not close the transaction", e);
                }

            }
        };
    }
}
