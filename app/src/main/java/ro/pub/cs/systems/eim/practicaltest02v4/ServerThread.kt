package ro.pub.cs.systems.eim.practicaltest02v4

import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.util.HashMap


class ServerThread(private val port: Int) : Thread() {

    private var serverSocket: ServerSocket? = null

    private val data: HashMap<String, String> = HashMap()

    override fun run() {
        try {
            serverSocket = ServerSocket(port)
            Log.v("[SERVER THREAD]", "Server started on port $port...")
            while (!currentThread().isInterrupted) {
                Log.i("[SERVER THREAD]", "Waiting for client...")
                val socket = serverSocket!!.accept()
                Log.i(
                    "[SERVER THREAD]",
                    "Client connected with IP: " + socket.inetAddress + ":" + socket.port
                )


                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (e: IOException) {
            Log.e("[SERVER THREAD]", "An exception has occurred: " + e.message)
        }
    }

    @Synchronized
    fun setData(url: String, body: String) {
        this.data[url] = body
    }

    @Synchronized
    fun getData(url: String): String? {
        return this.data[url]
    }

    fun stopThread() {
        interrupt()
        try {
            serverSocket?.close()
            Log.v("[SERVER THREAD]", "Server stopped...")
        } catch (e: IOException) {
            Log.e("[SERVER THREAD]", "An exception has occurred: " + e.message)
        }
    }
}