package com.amade.api.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSendException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(val javaMailSender: JavaMailSender) {
    @Value(value = "\${source.email.sender}")
    private val myEmail: String? = null

    suspend fun sendEmail(sendToEmail: String?, subject: String?, body: String?) {
        try {
            val sampleEmail = SimpleMailMessage()
            sampleEmail.setSubject(subject!!)
            sampleEmail.setFrom(myEmail!!)
            sampleEmail.setTo(sendToEmail)
            sampleEmail.setText(body!!)
            javaMailSender.send(sampleEmail)
        } catch (e: MailSendException) {
            println(e.failedMessages)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}