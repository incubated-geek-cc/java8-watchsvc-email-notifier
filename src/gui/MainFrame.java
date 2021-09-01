package gui;

import file.FileAdapter;
import file.FileEvent;
import file.FileWatcher;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;

/* https://docs.oracle.com/javase/tutorial/essential/io/examples/Email.java */
public class MainFrame extends JFrame {
    private final JPanel MAIN_PANEL;
    
    private final int FONT_SIZE=12;
    private final String FONT_FAMILY_NAME="Arial";
    
    private static final String LOGGER_NAME = MethodHandles.lookup().lookupClass().getName();
    private static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);

    private final JTextArea LOG_TEXT_AREA;
    private final JScrollPane JSCROLL_PANEL_OUTPUT_LOGS;

    private final JButton jButtonTestEmail;
    private final JLabel jLabelOutputFileLogsTitle;
    
    private final JLabel jLabelRecipientsEmailTitle;
    private final JTextField RECIPIENT_EMAIL_FIELD;
    
    private final JLabel jLabelSenderEmailTitle;
    private final JTextField SENDER_EMAIL_FIELD;
    
    private final JLabel jLabelSenderEmailSetPassword;
    private final JLabel jLabelEmailSettingsTitle;
    
    private final JButton jButtonSelectFolderTitle;
    private final JTextField WATCH_DIR ;
    
    private final JPasswordField PASSWORD_FIELD;
    
    private final JProgressBar JP;
    private String folderPathToMonitor;
    
    private String emailSubject="Welcome To Java Mail API";
    
    public MainFrame(String title) {
        super(title);
        LOG_TEXT_AREA = new JTextArea();
        LOG_TEXT_AREA.setEditable(false);
        LOG_TEXT_AREA.setWrapStyleWord(true);
        JSCROLL_PANEL_OUTPUT_LOGS = new JScrollPane(LOG_TEXT_AREA);
        
        JSCROLL_PANEL_OUTPUT_LOGS.setHorizontalScrollBar(null);
        DefaultCaret caret = (DefaultCaret) LOG_TEXT_AREA.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        //add components
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(new TextAreaHandler(new TextAreaOutputStream(LOG_TEXT_AREA)));
         
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MAIN_PANEL = new JPanel();
        MAIN_PANEL.setLayout(null);
        MAIN_PANEL.setBounds(10, 10, 795, 565);
        
        getContentPane().add(MAIN_PANEL, BorderLayout.CENTER);
        
        jLabelEmailSettingsTitle=new JLabel("Configure Email Notifications");
        jLabelEmailSettingsTitle.setBounds(20, 5, 715, 30);
        jLabelEmailSettingsTitle.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        MAIN_PANEL.add(jLabelEmailSettingsTitle);
        
        jLabelSenderEmailTitle= new JLabel("From:");
        jLabelSenderEmailTitle.setBounds(20, 35, 60, 30);
        jLabelSenderEmailTitle.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        
        SENDER_EMAIL_FIELD=new JTextField("someone@outlook.com");
        SENDER_EMAIL_FIELD.setBounds(80, 35, 270, 30);
        SENDER_EMAIL_FIELD.setFont(new Font(FONT_FAMILY_NAME, Font.PLAIN, FONT_SIZE));
        
        jLabelSenderEmailSetPassword= new JLabel("Password:");
        jLabelSenderEmailSetPassword.setBounds(355, 35, 80, 30);
        jLabelSenderEmailSetPassword.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        
        PASSWORD_FIELD=new JPasswordField("YourPassword");
        PASSWORD_FIELD.setBounds(430, 35, 365, 30);
        PASSWORD_FIELD.setFont(new Font(FONT_FAMILY_NAME, Font.PLAIN, FONT_SIZE));
        
        MAIN_PANEL.add(jLabelSenderEmailTitle);
        MAIN_PANEL.add(SENDER_EMAIL_FIELD);
        MAIN_PANEL.add(jLabelSenderEmailSetPassword);
        MAIN_PANEL.add(PASSWORD_FIELD);
        
        jLabelRecipientsEmailTitle=new JLabel("To:");
        jLabelRecipientsEmailTitle.setBounds(20, 75, 60, 30);
        jLabelRecipientsEmailTitle.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        
        RECIPIENT_EMAIL_FIELD=new JTextField("recipient@gmail.com");
        RECIPIENT_EMAIL_FIELD.setBounds(80, 75, 715, 30);
        RECIPIENT_EMAIL_FIELD.setFont(new Font(FONT_FAMILY_NAME, Font.PLAIN, FONT_SIZE));
        
        MAIN_PANEL.add(jLabelRecipientsEmailTitle);
        MAIN_PANEL.add(RECIPIENT_EMAIL_FIELD);
        
        jButtonTestEmail = new JButton("Send Email >>");
        jButtonTestEmail.setBounds(20, 120, 130, 30);
        jButtonTestEmail.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        MAIN_PANEL.add(jButtonTestEmail);
        
        JP=new JProgressBar(0, 100);
        JP.setBounds(170, 120, 625, 30); 
        JP.setStringPainted(true);
        JP.setVisible(false);
        MAIN_PANEL.add(JP);
        
        JLabel jLabelSetFolderTitle=new JLabel("Configure File Watcher");
        jLabelSetFolderTitle.setBounds(20, 170, 625, 30);
        jLabelSetFolderTitle.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        MAIN_PANEL.add(jLabelSetFolderTitle);
        
        jButtonSelectFolderTitle = new JButton("Select Folder...");
        jButtonSelectFolderTitle.setBounds(20, 205, 130, 30);
        jButtonSelectFolderTitle.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        MAIN_PANEL.add(jButtonSelectFolderTitle);
        
        WATCH_DIR = new JTextField("");
        WATCH_DIR.setBounds(165, 205, 625, 30);
        WATCH_DIR.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        MAIN_PANEL.add(WATCH_DIR);
        WATCH_DIR.setEnabled(false);
        
        jLabelOutputFileLogsTitle = new JLabel("Application File Log(s)");
        jLabelOutputFileLogsTitle.setBounds(20, 255, 775, 30);
        jLabelOutputFileLogsTitle.setFont(new Font(FONT_FAMILY_NAME, Font.BOLD, FONT_SIZE));
        MAIN_PANEL.add(jLabelOutputFileLogsTitle);
        
        JSCROLL_PANEL_OUTPUT_LOGS.setBounds(20, 285, 775, 275);
        LOG_TEXT_AREA.setFont(new Font("Consolas", Font.PLAIN, FONT_SIZE));
        MAIN_PANEL.add(JSCROLL_PANEL_OUTPUT_LOGS);
        
        setSize(835, 625);
        setLocationRelativeTo(null);
        setVisible(true);
        
        jButtonTestEmail.addActionListener((java.awt.event.ActionEvent evt) -> {
            startSendEmailAction(false, null);
        });
        
        jButtonSelectFolderTitle.addActionListener((java.awt.event.ActionEvent evt) -> {
            selectFolderToMonitor();
        });
    }
    
    private void selectFolderToMonitor() {
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                File selectedDir=null;
                JFileChooser chooser=new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setDialogTitle("Select Folder to Monitor...");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                
                int option = chooser.showOpenDialog(MAIN_PANEL);
                if(option == JFileChooser.APPROVE_OPTION) {
                    selectedDir = chooser.getSelectedFile();
                    if(selectedDir != null) {
                        folderPathToMonitor=selectedDir.toString();
                    }
                }
                if(selectedDir!=null) {
                    publish(folderPathToMonitor); // to be received by process
                }
                return folderPathToMonitor;
            }

            @Override
            protected void done() { // Can safely update the GUI from this method.
                String folderPathToMonitor;
                try {
                    folderPathToMonitor = get(); // Retrieve the return value of doInBackground.
                    if(folderPathToMonitor!=null) {
                        startWatchService(folderPathToMonitor);
                        outputConsoleLogsBreakline("Currently monitoring: " + folderPathToMonitor);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    outputConsoleLogsBreakline(e.getLocalizedMessage());
                }
            }

            @Override
            protected void process(List<String> chunks) { // Can safely update the GUI from this method.
                String mostRecentValue = chunks.get(chunks.size() - 1);
                if(mostRecentValue!=null) {
                    WATCH_DIR.setText(mostRecentValue);
                }
            }
        };
        worker.execute();
    }
    
    private void addTextToOutputLogs(String logString) {
        LOGGER.info(() -> logString);
    }

    private void outputConsoleLogsBreakline(String consoleCaption) {
        String logString = "";

        int charLimit = 180;
        if (consoleCaption.length() > charLimit) {
            logString = consoleCaption.substring(0, charLimit - 4) + " ...";
        } else {
            String result = "";

            if (consoleCaption.isEmpty()) {
                for (int i = 0; i < charLimit; i++) {
                    result += "=";
                }
                logString = result;
            } else {
                charLimit = (charLimit - consoleCaption.length() - 1);
                for (int i = 0; i < charLimit; i++) {
                    result += "-";
                }
                logString = consoleCaption + " " + result;
            }
        }
        logString = logString + "\n";
        addTextToOutputLogs(logString);
    }
    
    private void startWatchService(String folderPath) {
        File folder = new File(folderPath);
        FileWatcher watcher = new FileWatcher(folder);
        watcher.addListener(new FileAdapter() {
            @Override
            public void onCreated(FileEvent event) {
                File attachmentFile=event.getFile();
                String msgStr="[FILE CREATION] " + attachmentFile.getName() + " is created at "+getCurrentTimeStamp();
                emailSubject=msgStr;
                outputConsoleLogsBreakline(msgStr);
                startSendEmailAction(true, attachmentFile);
            }
            
            @Override
            public void onModified(FileEvent event) {
            File attachmentFile=event.getFile();
                String msgStr="[FILE MODIFICATION] " + attachmentFile.getName() + " is modified at "+getCurrentTimeStamp();
                emailSubject=msgStr;
                outputConsoleLogsBreakline(msgStr);
            }

            @Override
            public void onDeleted(FileEvent event) {
                File attachmentFile=event.getFile();
                String msgStr="[FILE DELETION] " + attachmentFile.getName() + " is deleted at "+getCurrentTimeStamp();
                emailSubject=msgStr;
                outputConsoleLogsBreakline(msgStr);
            }
        }).watch();
    }
    
    private String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mmaa");
        Date date = new Date();
        String timestamp = sdf.format(date);

        return timestamp;
    } 
    
    private void sendEmail(boolean hasAttachment, File attachmentFile) {
        try {
            String to = RECIPIENT_EMAIL_FIELD.getText();
            String from = SENDER_EMAIL_FIELD.getText();
            char[] passwordChars=PASSWORD_FIELD.getPassword();
            String passwordStr = "";
            for(char passwordChar:passwordChars) {
                passwordStr+=passwordChar;
            }
            final String password=passwordStr;
            
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp-mail.outlook.com"); // "smtp.gmail.com"
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, password);
                        }
                    });
            String msgBody = "Sending email using JavaMail API ["+getCurrentTimeStamp()+"]";
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "NoReply"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, "Dear Recipient"));
            
            msg.setSubject(emailSubject);
            
            BodyPart messageBodyPart1 = new MimeBodyPart();  
            messageBodyPart1.setText(msgBody);
            
            Multipart multipart = new MimeMultipart();  
            multipart.addBodyPart(messageBodyPart1);
            if(hasAttachment) {
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                String filename = attachmentFile.getName();//change accordingly  
                DataSource source = new FileDataSource(attachmentFile);  
                messageBodyPart2.setDataHandler(new DataHandler(source));  
                messageBodyPart2.setFileName(filename);
                multipart.addBodyPart(messageBodyPart2);  
            }
            msg.setContent(multipart);
            Transport.send(msg);
            outputConsoleLogsBreakline("Email sent successfully.");
        } catch (AddressException e) {
            System.out.println(e.getMessage());
            outputConsoleLogsBreakline(e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            outputConsoleLogsBreakline(e.getMessage());
        }
    }
    
    private void startSendEmailAction(boolean hasAttachment, File attachmentFile) {
        SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(25);
                    publish(i);
                }
                sendEmail(hasAttachment, attachmentFile);
                return true;
            }

            protected void done() { // Can safely update the GUI from this method.
                boolean status;
                try {
                    status = get(); // Retrieve the return value of doInBackground.
                    String displayStr="Error";
                    if(status) {
                        displayStr="Success";
                    }
                    outputConsoleLogsBreakline("Completion Status: " + displayStr);
                    RECIPIENT_EMAIL_FIELD.setEnabled(true);
                    SENDER_EMAIL_FIELD.setEnabled(true);
                    jButtonTestEmail.setEnabled(true);
                } catch (InterruptedException | ExecutionException e) {
                    outputConsoleLogsBreakline(e.getLocalizedMessage());
                }
            }

            @Override
            protected void process(List<Integer> chunks) {
                RECIPIENT_EMAIL_FIELD.setEnabled(false);
                SENDER_EMAIL_FIELD.setEnabled(false);
                jButtonTestEmail.setEnabled(false);

                int mostRecentValue = chunks.get(chunks.size() - 1);
                if(mostRecentValue==0) {
                    outputConsoleLogsBreakline("Sending Email...");
                    JP.setVisible(false);
                    JP.setValue(mostRecentValue);
                } else {
                    JP.setVisible(true);
                    JP.setValue(mostRecentValue);
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame("Watch Service Utilities :: v1.0");
            }
        });
    }
}
