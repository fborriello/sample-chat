package org.sample.chat.server;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.sample.chat.config.CommandPrefix.EXIT;
import static org.sample.chat.server.ClientHandler.CLIENT_NAMES;
import static org.sample.chat.server.ClientHandler.CLINT_WRITERS;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for class: {@link ClientHandler}.
 */
public class ClientHandlerTest {
    private static final String CLIENT_NAME_ONE = "John";
    private static final String CLIENT_NAME_TWO = "Anna";

    @Mock
    private Socket socket;

    private ClientHandler underTest;

    @BeforeClass
    public void setup() {
        initMocks(this);
        underTest = new ClientHandler(socket);
    }

    @BeforeMethod
    public void beforeMethod() {
        CLIENT_NAMES.clear();
        CLINT_WRITERS.clear();
    }

    @Test
    public void testHandleMessages() throws IOException {
        // WHEN
        Scanner in = mock(Scanner.class);
        OutputStream out = mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(out);
        when(in.nextLine()).thenReturn(CLIENT_NAME_ONE)
                .thenReturn("Message")
                .thenReturn(EXIT.getValue());

        // WHEN
        underTest.handleMessages(in);

        // THEN
        assertFalse(CLIENT_NAMES.contains(CLIENT_NAME_ONE));
        assertTrue(ClientHandler.CLINT_WRITERS.isEmpty());

    }

    @Test
    public void testGetClientName() {
        // GIVEN
        PrintWriter out = mock(PrintWriter.class);
        Scanner in = mock(Scanner.class);
        doNothing().when(out).println(anyString());
        when(in.nextLine()).thenReturn(CLIENT_NAME_ONE);

        // WHEN
        String actual = underTest.getClientName(out, in);

        // THEN
        assertEquals(CLIENT_NAME_ONE, actual);
    }

    @Test(dataProvider = "dataNotifyClientAlreadyInTest")
    public void testNotifyClientAlreadyIn(final List<String> clientNames, final String expectedOutMessage) {
        // GIVEN
        PrintWriter out = mock(PrintWriter.class);
        CLIENT_NAMES.addAll(clientNames);
        doNothing().when(out).println(anyString());

        // WHEN
        underTest.notifyClientAlreadyIn(CLIENT_NAME_ONE, out);

        // THEN
        verify(out, times(1)).println(expectedOutMessage);
        CLIENT_NAMES.clear();

    }

    @DataProvider
    public Object[][] dataNotifyClientAlreadyInTest() {
        return new Object[][]{
                {asList(CLIENT_NAME_ONE, CLIENT_NAME_TWO), "{new message} People already in: John,Anna"},
                {emptyList(), "{new message} " + CLIENT_NAME_ONE + " no one is online. Please wait for someone to start the conversation."}
        };
    }

    @Test
    public void testSendAll() {
        // GIVEN
        String expectedMessage = "{new message} Message";
        PrintWriter clientOnePrintWriter = mock(PrintWriter.class);
        PrintWriter clientTwoPrintWriter = mock(PrintWriter.class);
        CLINT_WRITERS.addAll(asList(clientOnePrintWriter, clientTwoPrintWriter));
        doNothing().when(clientOnePrintWriter).println(anyString());
        doNothing().when(clientTwoPrintWriter).println(anyString());

        // WHEN
        underTest.sendAll("Message");

        // THEN
        CLINT_WRITERS.forEach(printWriter -> verify(printWriter, times(1)).println(expectedMessage));
        CLINT_WRITERS.clear();
    }
}
