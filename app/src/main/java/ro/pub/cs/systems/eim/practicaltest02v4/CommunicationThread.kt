package ro.pub.cs.systems.eim.practicaltest02v4

import android.util.Log
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import okhttp3.Request


class CommunicationThread(private val serverThread: ServerThread, private val socket: Socket) : Thread() {

    override fun run() {
        try {
            // 1. Citim URL-ul trimis de client
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val printWriter = PrintWriter(socket.getOutputStream(), true)

            val urlFromClient = reader.readLine() // Să zicem că clientul trimite un URL

            if (urlFromClient != null && urlFromClient.isNotEmpty()) {

                var resultBody = serverThread.getData(urlFromClient)

                // 2. Dacă NU avem datele în cache-ul serverului, le descărcăm cu OkHttp
                if (resultBody == null) {
                    Log.i("[COMM THREAD]", "Data not in cache. Fetching via OkHttp...")

                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(urlFromClient)
                        .build()

                    try {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            // Aici extragem body-ul ca String
                            resultBody = response.body?.string()

                            // 3. SALVĂM în ServerThread (cerința ta principală)
                            if (resultBody != null) {
                                serverThread.setData(urlFromClient, resultBody)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("[COMM THREAD]", "Error fetching URL: ${e.message}")
                    }
                } else {
                    Log.i("[COMM THREAD]", "Data found in cache!")
                }

                // 4. Trimitem rezultatul înapoi la clientul Android
                if (resultBody != null) {
                    printWriter.println(resultBody)
                }
            }

            socket.close()
        } catch (e: Exception) {
            Log.e("[COMM THREAD]", "Exception: " + e.message)
        }
    }
}