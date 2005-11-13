/*
 *  Copyright 2001-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.buffer;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.collections.AbstractTestObject;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUnderflowException;
import org.apache.commons.collections.BufferUtils;

/**
 * @author James Carman
 * @version 1.0
 */
public class TestTimeoutBuffer extends AbstractTestObject {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final int FULL_SIZE = 100;

    private static final int TIMEOUT = 100;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static Test suite() {
        return new TestSuite( TestTimeoutBuffer.class );
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public TestTimeoutBuffer( String testName ) {
        super( testName );
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------
    public void testDecorationExceptions() {
        try {
            TimeoutBuffer.decorate((Buffer) null, 1);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            TimeoutBuffer.decorate(new CircularFifoBuffer(4), -1);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public String getCompatibilityVersion() {
        return "3.2";
    }

    public boolean isEqualsCheckable() {
        return false;
    }

    public Object makeObject() {
        return BufferUtils.timeoutBuffer( new ArrayStack(), TIMEOUT );
    }

    public void testEmptySerialization() {
        try {
            final TimeoutBuffer b = ( TimeoutBuffer ) readExternalFormFromDisk(
                    getCanonicalEmptyCollectionName( makeObject() ) );
            assertTrue( b.isEmpty() );
        }
        catch( Exception e ) {
            fail( "Could not read object from disk." );
        }
    }

    public void testFullSerialization() {
        try {
            final TimeoutBuffer b = ( TimeoutBuffer ) readExternalFormFromDisk(
                    getCanonicalFullCollectionName( makeObject() ) );
            assertEquals( FULL_SIZE, b.size() );
        }
        catch( Exception e ) {
            fail( "Could not read object from disk." );
        }
    }

    public void testSuccessfulWaitOnGet() {
        Buffer b = ( Buffer ) makeObject();
        executeAsynchronously( new Getter( b ) );
        executeAsynchronously( new Adder( b, "Hello" ) );
    }

    private static void executeAsynchronously( Runnable r ) {
        new Thread( r ).start();
    }

    public void testSuccessfulWaitOnRemove() {
        Buffer b = ( Buffer ) makeObject();
        executeAsynchronously( new Remover( b ) );
        executeAsynchronously( new Adder( b, "Hello" ) );
    }

    public void testTimeoutOnGet() {
        final Buffer buffer = makeBuffer();
        try {
            Getter remover = new Getter( buffer );
            executeAsynchronously( remover );
            executeAsynchronously( new Adder( buffer, "Howdy" ), TIMEOUT * 2 );
            assertFalse( remover.isSuccesful() );
        }
        catch( BufferUnderflowException e ) {
        }
    }

    private TimeoutBuffer makeBuffer() {
        return ( TimeoutBuffer ) makeObject();
    }

    private static void executeAsynchronously( Runnable r, long delay ) {
        new Thread( new DelayedRunnable( r, delay ) ).start();
    }

    public void testTimeoutOnRemove() {
        final Buffer buffer = makeBuffer();
        try {
            Remover remover = new Remover( buffer );
            executeAsynchronously( remover );
            executeAsynchronously( new Adder( buffer, "Howdy" ), TIMEOUT * 2 );
            assertFalse( remover.isSuccesful() );
        }
        catch( BufferUnderflowException e ) {
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private static class DelayedRunnable implements Runnable {
        private final Runnable r;

        private final long delay;
        public DelayedRunnable( Runnable r, long delay ) {
            this.r = r;
            this.delay = delay;
        }

        public void run() {
            try {
                Thread.sleep( delay );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            r.run();
        }
    }

    private static class Adder implements Runnable {
        private final Buffer b;

        private final Object o;
        public Adder( Buffer b, Object o ) {
            this.b = b;
            this.o = o;
        }

        public void run() {
            b.add( o );
        }
    }

    private static class Remover extends BufferReader {
        public Remover( Buffer b ) {
            super( b );
        }

        protected void performOperation() {
            b.remove();
        }
    }

    private static abstract class BufferReader implements Runnable {

        protected final Buffer b;
        private Boolean succesful;

        protected BufferReader( Buffer b ) {
            this.b = b;
        }

        protected abstract void performOperation();

        public final synchronized void run() {
            try {
                performOperation();
                succesful = Boolean.TRUE;
            }
            catch( BufferUnderflowException e ) {
                succesful = Boolean.FALSE;
            }
            notifyAll();
        }

        public synchronized boolean isSuccesful() {
            while( succesful == null ) {
                try {
                    wait();
                }
                catch( InterruptedException e ) {
                }
            }
            return succesful.booleanValue();
        }
    }

    private static class Getter extends BufferReader {
        public Getter( Buffer b ) {
            super( b );
        }

        protected void performOperation() {
            b.get();
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// main() method
//----------------------------------------------------------------------------------------------------------------------

    public static void main( String args[] ) {
        String[] testCaseName = {TestTimeoutBuffer.class.getName()};
        junit.textui.TestRunner.main( testCaseName );
    }
}

