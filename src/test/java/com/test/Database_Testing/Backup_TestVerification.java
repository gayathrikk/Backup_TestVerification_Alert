	package com.test.Database_Testing;
	
	import org.testng.annotations.Test;
	
	import javax.mail.*;
	import javax.mail.internet.*;
	import java.sql.*;
	import java.util.*;
	
	public class Backup_TestVerification {
	
	    @Test
	    public void checkStatus6AndSendAlert() {
	        String url = "jdbc:mysql://apollo2.humanbrain.in:3306/HBA_V2";
	        String username = "root";
	        String password = "Health#123";
	
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            System.out.println("✅ MySQL JDBC Driver Registered");
	
	            try (Connection conn = DriverManager.getConnection(url, username, password)) {
	
	                String query = "SELECT sd.bio_id, sd.bio_name " +
	                               "FROM storage_backup sb " +
	                               "JOIN storage_details sd ON sb.storage_details = sd.id " +
	                               "WHERE sb.status = 6";
	
	                try (PreparedStatement stmt = conn.prepareStatement(query);
	                     ResultSet rs = stmt.executeQuery()) {
	
	                    List<String> biosampleIds = new ArrayList<>();
	                    List<String> brainNames = new ArrayList<>();
	
	                    while (rs.next()) {
	                        biosampleIds.add(rs.getString("bio_id"));
	                        brainNames.add(rs.getString("bio_name"));
	                    }
	
	                    if (!biosampleIds.isEmpty()) {
	                        System.out.println("⚠ Records with status 6 found. Sending verification pending email...");
	                        sendEmailVerificationPending(biosampleIds, brainNames);
	                    } else {
	                        System.out.println("✅ No records with status 6. No action needed.");
	                    }
	                }
	
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	    private void sendEmailVerificationPending(List<String> biosampleIds, List<String> brainNames) {
	      final String from = "automationsoftware25@gmail.com";
	      final String password = "wjzcgaramsqvagxu"; // App Password
	        String[] to = {"gayuriche26@gmail.com"};
	        String subject = "⚠ Backup Verification Pending Alert";
	
	        Properties props = new Properties();
	        props.put("mail.smtp.host", "smtp.gmail.com");
	        props.put("mail.smtp.port", "465");
	        props.put("mail.smtp.ssl.enable", "true");
	        props.put("mail.smtp.auth", "true");
	
	        Session session = Session.getInstance(props, new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(from, password);
	            }
	        });
	
	        // Build HTML table
	        StringBuilder tableBuilder = new StringBuilder();
	        tableBuilder.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>");
	        tableBuilder.append("<tr style='background-color: #f2f2f2;'><th>Biosample ID</th><th>Brain Name</th></tr>");
	
	        for (int i = 0; i < biosampleIds.size(); i++) {
	            tableBuilder.append("<tr>");
	            tableBuilder.append("<td>").append(biosampleIds.get(i)).append("</td>");
	            tableBuilder.append("<td>").append(brainNames.get(i)).append("</td>");
	            tableBuilder.append("</tr>");
	        }
	
	        tableBuilder.append("</table>");
	
	        try {
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));
	            for (String recipient : to) {
	                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
	            }
	
	            message.setSubject(subject);
	
	            String body = "<html><body>" +
	                    "<p><b>Backup verification is pending for the following Brains:</b></p>" +
	                    tableBuilder.toString() +
	                    "<p style='color:gray;font-size:small;'>This is an automatically generated email. Please do not reply.</p>" +
	                    "</body></html>";
	
	            message.setContent(body, "text/html");
	
	            Transport.send(message);
	            System.out.println("📧 Verification pending email sent successfully!");
	
	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	    }
	}
