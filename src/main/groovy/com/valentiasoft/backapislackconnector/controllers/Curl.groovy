package com.valentiasoft.backapislackconnector.controllers

class Curl {

    void call(String endpoint, String method, Map flags){
        String command = "curl -X ${method} ${endpoint}"
        flags.each {key, value ->
            command += " -${key} ${value}"
        }
        Process pr = ['sh', '-c', "${command}"].execute()

        String curlLog = """|Curl executed:
                            |- Command: 'curl -X ${method} https://hooks.slack.com/services/XXXX/YYYY/ZZZZ'
                            |- Flags: ${ flags }
                            |- Output: ${pr.in.text}
                            |- Errors: ${pr.err.text}
                            |""".stripMargin()
        println(curlLog)
    }
}
