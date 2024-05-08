package PDFGenerator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class PDFGenerator {
    private String htmlTemplateUrl;
    private String outputPdf;
    private JSONObject payrollDetailJson;
    private String smtpHost;
    private String smtpPort;
    private String senderEmail;
    private String senderPassword;

    public PDFGenerator(String htmlTemplateUrl, String outputPdf, JSONObject payrollDetailJson, String smtpHost, String smtpPort, String senderEmail, String senderPassword) {
        this.htmlTemplateUrl = htmlTemplateUrl;
        this.outputPdf = outputPdf;
        this.payrollDetailJson = payrollDetailJson;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    public void generateAndSavePDF() throws IOException {
        replaceData();
        byte[] pdfData = generatePDFData();
        savePDF(pdfData);
        JOptionPane.showMessageDialog(null, "PDF guardado con exito");

    }

    public void generateAndSendPDF() throws IOException, MessagingException {
        replaceData();
        byte[] pdfData = generatePDFData();
        String tempFilePath = savePDFTemporarily(pdfData);
        sendEmail(tempFilePath);
        JOptionPane.showMessageDialog(null, "Correo electrónico enviado con éxito");
    }

    private byte[] generatePDFData() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);

            // Convertir el documento de Jsoup a W3C DOM
            W3CDom w3cDom = new W3CDom();
            org.w3c.dom.Document w3cDocument = w3cDom.fromJsoup(initHTMLTemplate());

            renderer.setDocument(w3cDocument, null);
            renderer.layout();
            renderer.createPDF(outputStream);
        } finally {
            outputStream.close();
        }
        return outputStream.toByteArray();
    }

    public void savePDF(byte[] pdfData) throws IOException {
        // Crear un JFileChooser para que el usuario seleccione la ruta de guardado
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String outputPdfPath = fileToSave.getAbsolutePath();

            // Concatenar ".pdf" al directorio seleccionado si no tiene una extensión de archivo
            if (!outputPdfPath.toLowerCase().endsWith(".pdf")) {
                outputPdfPath += ".pdf";
            }

            try (OutputStream outputStream = new FileOutputStream(outputPdfPath)) {
                outputStream.write(pdfData);
            }
            outputPdf = outputPdfPath;
        }
    }

    private String savePDFTemporarily(byte[] pdfData) throws IOException {
        File tempFile = File.createTempFile("temp-pdf", ".pdf");
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(pdfData);
        }
        return tempFile.getAbsolutePath();
    }
    private String getRemitentEmail(){
        // Obtener el correo electrónico del remitente fron payrollDetailJson.employee.employeeEmail
        return payrollDetailJson.getJSONObject("employee").getString("employeeEmail");
    }
    private void sendEmail(String attachmentPath) throws MessagingException, IOException {
        // Configurar propiedades para la conexión SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.port", smtpPort);

        // Autenticación del remitente
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        // Crear el mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail)); // Remitente
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getRemitentEmail())); // Destinatario
        message.setSubject("Boleta de Pago"); // Asunto del correo electrónico
        message.setText("Body of the Email"); // Cuerpo del correo electrónico

        // Adjuntar el archivo PDF al mensaje
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(attachmentPath));

        // Agregar el archivo adjunto al mensaje
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(attachmentBodyPart);
        message.setContent(multipart);

        // Enviar el mensaje
        Transport.send(message);
    }
    public Document initHTMLTemplate() throws IOException {
        File inputHTML = new File(htmlTemplateUrl);
        Document document = Jsoup.parse(inputHTML, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document;
    }

    public void replaceData() throws IOException {
        Context context = new Context();
        for (String key : payrollDetailJson.keySet()) {
            if (payrollDetailJson.get(key) instanceof JSONObject) {
                JSONObject jsonObject = payrollDetailJson.getJSONObject(key);
                for (String innerKey : jsonObject.keySet()) {
                    context.setVariable(innerKey, jsonObject.get(innerKey));
                }
            } else if (payrollDetailJson.get(key) instanceof JSONArray) {
                JSONArray jsonArray = payrollDetailJson.getJSONArray(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String keyContext = "";
                    int amountContext = 0;
                    if (jsonObject.has("conceptName")) {
                        keyContext = jsonObject.getString("conceptName");
                    }
                    if (jsonObject.has("conceptAmount")) {
                        amountContext = jsonObject.getInt("conceptAmount");
                    }
                    context.setVariable(keyContext, amountContext);
                }
            } else {
                context.setVariable(key, payrollDetailJson.get(key));
            }
        }

        // Obtener el HTML como cadena
        String htmlContent = loadHTMLTemplateAsString();

        // Procesar la plantilla HTML
        TemplateEngine templateEngine = new TemplateEngine();
        String processedHtml = templateEngine.process(htmlContent, context);

        // Escribir el HTML procesado en un archivo temporal
        String outputHtmlFilePath = "output.html";
        try (PrintWriter writer = new PrintWriter(outputHtmlFilePath, "UTF-8")) {
            writer.println(processedHtml);
        }

        // Actualizar la URL de la plantilla HTML con la nueva ruta de archivo
        htmlTemplateUrl = outputHtmlFilePath;
    }

    public String loadHTMLTemplateAsString() throws IOException {
        // Cargar el HTML del archivo en una cadena
        byte[] bytes = Files.readAllBytes(Paths.get(htmlTemplateUrl));
        return new String(bytes, "UTF-8");
    }
}
