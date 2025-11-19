package su.kawunprint.services

import io.github.cdimascio.dotenv.dotenv
import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.*

class EmailService {

    private val dotenv = dotenv()
    private val smtpHost = dotenv["SMTP_HOST"] ?: "smtp.gmail.com"
    private val smtpPort = dotenv["SMTP_PORT"] ?: "587"
    private val smtpUser = dotenv["SMTP_USER"] ?: ""
    private val smtpPassword = dotenv["SMTP_PASSWORD"] ?: ""
    private val fromEmail = dotenv["FROM_EMAIL"] ?: smtpUser
    private val fromName = dotenv["FROM_NAME"] ?: "KawunPrint"

    fun sendVerificationCode(toEmail: String, code: String) {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(smtpUser, smtpPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail, fromName))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "Email Verification Code - KawunPrint"

                val htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                            .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                            .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; }
                            .code { font-size: 32px; font-weight: bold; color: #4CAF50; text-align: center; 
                                    letter-spacing: 5px; padding: 20px; background-color: white; 
                                    border-radius: 5px; margin: 20px 0; }
                            .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>KawunPrint</h1>
                            </div>
                            <div class="content">
                                <h2>Email Verification</h2>
                                <p>Hello!</p>
                                <p>Thank you for registering with KawunPrint. To complete your registration, 
                                   please use the following verification code:</p>
                                <div class="code">$code</div>
                                <p>This code will expire in 15 minutes.</p>
                                <p>If you didn't request this code, please ignore this email.</p>
                            </div>
                            <div class="footer">
                                <p>© 2025 KawunPrint. All rights reserved.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """.trimIndent()

                setContent(htmlContent, "text/html; charset=utf-8")
            }

            Transport.send(message)
            println("✅ Verification code sent to: $toEmail")
        } catch (e: Exception) {
            println("❌ Failed to send email to $toEmail: ${e.message}")
            e.printStackTrace()
            throw RuntimeException("Failed to send verification email", e)
        }
    }

    fun sendPasswordResetEmail(toEmail: String, newPassword: String) {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(smtpUser, smtpPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail, fromName))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "Password Reset - KawunPrint"

                val htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                            .header { background-color: #FF5722; color: white; padding: 20px; text-align: center; }
                            .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; }
                            .password { font-size: 24px; font-weight: bold; color: #FF5722; text-align: center; 
                                    letter-spacing: 2px; padding: 20px; background-color: white; 
                                    border-radius: 5px; margin: 20px 0; font-family: monospace; }
                            .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                            .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; 
                                    padding: 15px; margin: 20px 0; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>KawunPrint</h1>
                            </div>
                            <div class="content">
                                <h2>Password Reset</h2>
                                <p>Hello!</p>
                                <p>Your password has been reset. Here is your new temporary password:</p>
                                <div class="password">$newPassword</div>
                                <div class="warning">
                                    <strong>⚠️ Important:</strong> Please change this password immediately after logging in 
                                    for security reasons. Go to your profile settings to set a new password.
                                </div>
                                <p>If you didn't request a password reset, please contact support immediately.</p>
                            </div>
                            <div class="footer">
                                <p>© 2025 KawunPrint. All rights reserved.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """.trimIndent()

                setContent(htmlContent, "text/html; charset=utf-8")
            }

            Transport.send(message)
            println("✅ Password reset email sent to: $toEmail")
        } catch (e: Exception) {
            println("❌ Failed to send password reset email to $toEmail: ${e.message}")
            e.printStackTrace()
            throw RuntimeException("Failed to send password reset email", e)
        }
    }
}
