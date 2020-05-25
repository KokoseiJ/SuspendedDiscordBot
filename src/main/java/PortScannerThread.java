import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class PortScannerThread extends Thread {

    private final MessageReceivedEvent event;
    private final EmbedBuilder eb;
    private final User user;
    private final TextChannel textChannel;
    private final String[] args;
    private String command;
    private final boolean isSecMsg;
    private final String secMsgId;

    public PortScannerThread(MessageReceivedEvent event, String[] args, String command, boolean isSecMsg, String secMsgId) {
        EmbedBuilder eb = new EmbedBuilder();
        this.event = event;
        this.eb = eb;
        this.user = event.getAuthor();
        this.textChannel = event.getTextChannel();
        this.args = args;
        this.command = command;
        this.isSecMsg = isSecMsg;
        this.secMsgId = secMsgId;
    }

    @Override

    public void run() {
        try {
            String logCommand = "";
            //if(user.getAsTag().equals("PRASEOD-#8621")) return;
            if (args.length > 3) {
                boolean allPort = false, spPort = false;
                if(args[1].equalsIgnoreCase("TCP"))
                {
                    if ((args[3].equals("all") && args.length < 5)  || (args[3].equals("1-65535") && args.length < 5)) {
                        logCommand = command + ", Ports 1-65535";
                        command = "-T5 -Pn -p1-65535 " + command;
                        allPort = true;
                    } else if ((TextListener.isPortSpecific(args[3]) && args.length < 5)) {
                        boolean isMultiTarget = false;
                        for (int i = 0; i < args[3].length(); i++) {
                            if (args[3].charAt(i) == '-') {
                                logCommand = command + ", Ports " + args[3];
                                isMultiTarget = true;
                                break;
                            }
                        }
                        if (!isMultiTarget) logCommand = command + ", Port " + args[3];
                        command = "-p" + args[3] + " " + command;
                        spPort = true;
                    }
                }else if(args[1].equalsIgnoreCase("UDP"))
                {
                    command = "-T5 -sU " + command;

                    if ((args[3].equals("all") && args.length < 5) || (args[3].equals("1-65535") && args.length < 5)) {
                        eb.setTitle("UDP 스캔은 전체 스캔을 지원하지 않습니다!");
                        eb.setDescription("너무 오래걸려요...");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    } else if ((TextListener.isPortSpecific(args[3]) && args.length < 5)) {
                        boolean isMultiTarget = false;
                        for (int i = 0; i < args[3].length(); i++) {
                            if (args[3].charAt(i) == '-') {
                                logCommand = command + ", Ports " + args[3];
                                isMultiTarget = true;
                                break;
                            }
                        }
                        if (!isMultiTarget) logCommand = command + ", Port " + args[3];
                        command = "-p" + args[3] + " " + command;
                        logCommand = logCommand.replace("-T5 -sU ", "");
                        spPort = true;
                    }
                }

                if (allPort) {
                    if(isSecMsg) Thread.sleep(2000);
                    eb.setTitle("스캔 중 입니다.");
                    eb.setDescription("전체 포트 스캔은 시간이 정말 많이 걸립니다.. 기다려 주세요..");
                    eb.setFooter("요청자 : " + user.getAsTag());
                } else if (spPort) {
                    if(isSecMsg) Thread.sleep(2000);
                    eb.setTitle("스캔 중 입니다.");
                    eb.setDescription("특정 범위 포트 스캔은 상황에 따라 시간이 걸립니다.. 기다려 주세요.");
                    eb.setFooter("요청자 : " + user.getAsTag());
                } else {
                    textChannel.deleteMessageById(secMsgId).queue();
                    eb.setTitle("인자 오류!");
                    eb.setDescription("오타가 발생했네요..");
                    eb.setFooter("요청자 : " + user.getAsTag());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }
            } else {
                if(args[1].equalsIgnoreCase("UDP")) command = "-T5 -sU " + command;
                logCommand = command + ", Ports Well-known(Default)";
                logCommand = logCommand.replace("-T5 -sU ", "");

                if(isSecMsg) Thread.sleep(2000);
                eb.setTitle("스캔 중 입니다.");
                eb.setDescription("잠시만 기다려 주세요...");
                eb.setFooter("요청자 : " + user.getAsTag());
            }
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] messageDigest = md.digest((user.getId() + "--" + new String(Base64.getEncoder().encode(logCommand.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8) + "--l5L5KBIhZ25cVED0otdKr0lD4essUsXe").getBytes());
            BigInteger no = new BigInteger(1, messageDigest);

            String[] param = (user.getId() + "\u200B" + new String(Base64.getEncoder().encode(logCommand.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8) + "\u200B" + no.toString(16)).split("\u200B");
            SQL.insertSQLData(Main.con, "nmap_log", param, event);

            RestAction<Message> ra = null;
            if(isSecMsg) ra = textChannel.editMessageById(secMsgId, eb.build());
            else ra = textChannel.sendMessage(eb.build());

            Message myMessage = ra.complete();
            String messageId = myMessage.getId();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        Runtime
                                .getRuntime()
                                .exec("nmap " + command)
                                .getInputStream(), StandardCharsets.UTF_8));

                ArrayList<String> output = new ArrayList<>();
                String i;
                while ((i = br.readLine()) != null) {
                    output.add(i);
                }

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = format.format(date);
                if(args[1].equalsIgnoreCase("TCP")) output.set(0, "TCP Port Scanned at " + time);
                else output.set(0, "UDP Port Scanned at " + time);

                StringBuilder PORT = new StringBuilder();
                StringBuilder STATE = new StringBuilder();
                StringBuilder SERVICE = new StringBuilder();

                for (int j = 1; j < output.size(); j++) {
                    if (output.get(j).contains("Nmap scan report")) {
                        output.set(j, output.get(j).replaceAll("Nmap scan report", "Scanning report"));
                        eb.setDescription(output.get(j));
                    } else if (output.get(j).contains("Host seems down")) {

                        eb.setTitle("Host seems down. Can't connect this Host.");
                        eb.setDescription("Scanning report for " + args[2]);
                        eb.setFooter("요청자 : " + user.getAsTag());
                        textChannel.editMessageById(messageId, eb.build()).queue();
                        return;
                    } else if (output.get(j).contains("0 IP addresses")) {

                        eb.setTitle("Host cannot Resolved. Is it a valid Host address?");
                        eb.setDescription("It is more likely to be a typo.");
                        eb.setFooter("요청자 : " + user.getAsTag());
                        textChannel.editMessageById(messageId, eb.build()).queue();
                        return;
                    }

                    while (!output.get(j).startsWith("Nmap done")) {

                        if (output.get(j).contains("tcp") || output.get(j).contains("udp")) {
                            String tmp = output.get(j).trim();
                            tmp = tmp.replace("tcpmux", "t\u200Bc\u200Bpmux");

                            if (tmp.contains("tcp")) {
                                PORT.append(tmp, 0, tmp.indexOf("tcp") + 3).append("\n");
                                STATE.append(TextListener.CheckState(tmp)).append("\n");
                                SERVICE.append(tmp.replace(tmp.substring(0, tmp.indexOf("tcp") + 3), "").replace(TextListener.CheckState(tmp), "")).append("\n");
                                j++;
                            } else if (tmp.contains("udp")) {
                                PORT.append(tmp, 0, tmp.indexOf("udp") + 3).append("\n");
                                STATE.append(TextListener.CheckState(tmp)).append("\n");
                                SERVICE.append(tmp.replace(tmp.substring(0, tmp.indexOf("udp") + 3), "").replace(TextListener.CheckState(tmp), "")).append("\n");
                                j++;
                            }
                        } else j++;
                    }
                }

                if (PORT.toString().equals("") && STATE.toString().equals("") && SERVICE.toString().equals("")) {

                    eb.setTitle("The Host is Really up, but Scanned Port couldn't found.");
                    eb.setDescription("Try Scaning Specific Ports or Using 'All' Arguments.");
                    eb.setFooter("요청자 : " + user.getAsTag());
                    textChannel.editMessageById(messageId, eb.build()).queue();
                    return;
                }

                eb.setTitle(output.get(0));
                eb.addField("PORT", PORT.toString(), true);
                eb.addField("STATE", STATE.toString(), true);
                eb.addField("SERVICE", SERVICE.toString(), true);
                eb.setFooter("요청자 : " + user.getAsTag());
                textChannel.editMessageById(messageId, eb.build()).queue();

            } catch (IOException e) {
                eb.setTitle("nmap을 사용할 수 없어요...");
                eb.setDescription("nmap을 설치 해 주세요!");
                eb.setFooter("요청자 : " + user.getAsTag());
                textChannel.sendMessage(eb.build()).queue();
            }
        } catch (Exception e) {
            eb.setTitle("쓰레드를 실행할 수 없습니다.");
            textChannel.sendMessage(eb.build()).queue();
        }
    }
}
