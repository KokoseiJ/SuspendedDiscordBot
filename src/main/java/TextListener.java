import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.io.FileUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TextListener extends ListenerAdapter {
    public static boolean[] stateVar = {false};

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User user = event.getAuthor();
        TextChannel textChannel = event.getTextChannel();
        Message message = event.getMessage();
        EmbedBuilder eb = new EmbedBuilder();
        Guild guild = event.getGuild();

        if (user.isBot()) return;
        if (event.isFromType(ChannelType.PRIVATE)) return;
        if (message.getContentRaw().contains(";;")) {
            textChannel.sendMessage("하라는 코딩은 안하고!").queue();
            return;
        }

        if (message.getMentionedUsers().size() > 0 && message.getMentionedUsers().get(0).getId().equals("345473282654470146"))
            textChannel.sendMessage("무요").queue();

        //textChannel.sendMessage(message.getMentionedUsers().get(0)+ "").queue();
        try {
            message.getContentRaw().charAt(0);
        } catch (StringIndexOutOfBoundsException e) {
            return;
        }

        if (message.getContentRaw().length() < 3) return;

        if (message.getContentRaw().charAt(0) == '!' && message.getContentRaw().charAt(1) == '!') {
            String[] args = message.getContentRaw().substring(2).split(" ");
            if (args.length <= 0) return;

            if (args[0].equalsIgnoreCase("cc") || args[0].equalsIgnoreCase("clean")) {
                if (getHelpString.check(textChannel, args)) return;

                if (args.length != 2) return;

                int count;

                try {
                    count = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    textChannel.sendMessage("숫자가 아닙니다!").queue();
                    return;
                }

                if (count < 2) {
                    textChannel.sendMessage("숫자가 너무 작아요!").queue();
                    return;
                }
                if (count > 99) {
                    textChannel.sendMessage("그럴바엔 채널을 지우지..").queue();
                    return;
                }

                MessageHistory messageHistory = new MessageHistory(textChannel);
                List<Message> messages = messageHistory.retrievePast(count).complete();
                try {
                    textChannel.deleteMessages(messages).complete();
                } catch (PermissionException e) {
                    textChannel.sendMessage("채팅을 지울 수 있는 권한이 없어요!").queue();
                    return;
                }
                textChannel.sendMessage(user.getAsTag() + " 유저가 " + count + "개의 메세지를 삭제했습니다.").queue();
                return;
            }

            if (args[0].equalsIgnoreCase("port-scanner")) {

                boolean isSecMsg = false;
                String secMsgId = "";
                if (getHelpString.check(textChannel, args)) return;

                if (args[1].equalsIgnoreCase("blocklist")) {


                    if (args.length < 3) {
                        textChannel.sendMessage(getHelpString.main()).queue();
                        return;
                    }

                    if (args[2].equalsIgnoreCase("add")) {

                        if (args.length < 4) {
                            textChannel.sendMessage(getHelpString.main()).queue();
                            return;
                        }

                        if (user.getId().equals("345473282654470146")) {
                            PortScannerBlocklistsSettings.add(args[3], event);
                            Main.Blocklists = PortScannerBlocklistsSettings.read();
                            eb.setTitle("성공적으로 블록리스트 대상를 추가했어요!");
                            eb.setDescription("```Java\n추가된 주소: \"" + args[3] + "\"\n```");
                        } else {
                            eb.setTitle("이봐... 너는 내가 아니야..");
                            eb.setDescription("어이어이.. 돌아가라구..");

                        }
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    } else if (args[2].equalsIgnoreCase("remove")) {

                        if (args.length < 4) {
                            textChannel.sendMessage(getHelpString.main()).queue();
                            return;
                        }

                        if (!PortScannerBlocklistsSettings.remove(args[3], event)) {
                            eb.setTitle("입력한 주소를 찾을 수 없어요..");
                            eb.setDescription("정말 그 주소가 블록리스트 대상인가요?");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }
                        Main.Blocklists = PortScannerBlocklistsSettings.read();
                        if (user.getId().equals("345473282654470146")) {
                            eb.setTitle("성공적으로 블록리스트 대상를 제거했어요!");
                            eb.setDescription("```Java\n제거된 주소: \"" + args[3] + "\"\n```");
                        } else {
                            eb.setTitle("이봐... 너는 내가 아니야..");
                            eb.setDescription("어이어이.. 돌아가라구..");

                        }
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    } else if (args[2].equalsIgnoreCase("show")) {

                        if (args.length >= 4) {
                            printErrorArgs(user, eb, textChannel);
                            return;
                        }
                        Main.Blocklists = PortScannerBlocklistsSettings.read();

                        eb.setTitle("Blocklists are :");
                        eb.setDescription(String.join("\\*, ", Main.Blocklists) + "\\*");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    } else {
                        printErrorArgs(user, eb, textChannel);
                        return;
                    }
                } else if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("list")) {
                    eb.setTitle("제 생각엔 오타를 치신 것 같은데요...?");
                    eb.setDescription("맞죠?");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }
                RestAction<Message> raeb = null;
                if (!(args[1].equalsIgnoreCase("TCP") || args[1].equalsIgnoreCase("UDP"))) {
                    StringBuilder newArgs = new StringBuilder();
                    eb.setTitle("스캔할 프로토콜을 적지 않으셨군요!");
                    eb.setDescription("TCP 모드로 스캔합니다.");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    raeb = textChannel.sendMessage(eb.build());

                    for (int i = 0; i < args.length; i++) {
                        if (args[i].equals("port-scanner")) args[i] = "port-scanner TCP";
                        newArgs.append(args[i]).append(" ");
                    }
                    args = newArgs.toString().split(" ");
                    isSecMsg = true;
                }

                String command = args[2];
                if (areYouCrazy(command, Main.Blocklists)) {
                    eb.setTitle("이 주소는 블록리스트에 추가 되어 있어요.");
                    eb.setDescription("위험한 주소인 것 같군요..!");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }

                Message secMessage = null;
                if (raeb != null) {
                    secMessage = raeb.complete();
                    secMsgId = secMessage.getId();
                }

                Thread portScannerThread = new PortScannerThread(event, args, command, isSecMsg, secMsgId);
                portScannerThread.start();

            } else if (args[0].equalsIgnoreCase("help")) {
                textChannel.sendMessage(getHelpString.main()).queue();
            } else if (args[0].equalsIgnoreCase("getNamesByID")) {
                if (stateVar[0]) {
                    eb.setTitle("누군가가 아직 사용하고 있어요..!");
                    eb.setDescription("잠시만 기다려 주세요..");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }

                StringBuilder builder = new StringBuilder();
                guild.getMembers().forEach(m -> builder.append(m.getUser().getId()).append(" ").append(m.getUser().getName()).append("#").append(m.getUser().getDiscriminator()).append("\n"));

                try {
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            stateVar[0] = true;
                        }
                    };

                    timer.schedule(timerTask, 10000);

                    eb.setTitle("암호를 입력하세요. (제한 시간 10초)");
                    eb.setDescription("요청자 : " + user.getAsTag());
                    RestAction<Message> raeb = textChannel.sendMessage(eb.build());

                    Message myMessage = raeb.complete();
                    String ebMessageId = myMessage.getId();

                    textChannel = event.getTextChannel();
                    String messageId;

                    while (true) {
                        if (!stateVar[0]) {
                            textChannel = event.getTextChannel();
                            MessageHistory messageHistory = new MessageHistory(textChannel);
                            List<Message> messages = messageHistory.retrievePast(1).complete();
                            message = messages.get(0);

                            if (message.getAuthor().getAsTag().equals(user.getAsTag())) {
                                try {
                                    String decodedKey = new String(Base64.getDecoder().decode(message.getContentRaw().replace("key=", "")));
                                    if (decodedKey.equalsIgnoreCase("my name is bogdam, code name bogdam")) {
                                        messageId = message.getId();
                                        break;
                                    } else {
                                        timerTask.cancel();
                                        stateVar[0] = false;
                                        textChannel.deleteMessageById(ebMessageId).queue();
                                        eb.setTitle("올바르지 않는 암호입니다!");
                                        eb.setDescription("그대여... 무엇을 원하는가...");
                                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                                        textChannel.sendMessage(eb.build()).queue();
                                        return;
                                    }

                                } catch (IllegalArgumentException e) {
                                    timerTask.cancel();
                                    stateVar[0] = false;
                                    textChannel.deleteMessageById(ebMessageId).queue();
                                    eb.setTitle("올바르지 않는 암호입니다!");
                                    eb.setDescription("그대여... 무엇을 원하는가...");
                                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                                    textChannel.sendMessage(eb.build()).queue();
                                    return;
                                }
                            }
                        } else {
                            stateVar[0] = false;
                            textChannel.deleteMessageById(ebMessageId).queue();
                            eb.setTitle("제한 시간 초과!");
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }
                    }
                    timerTask.cancel();
                    stateVar[0] = false;
                    textChannel.deleteMessageById(ebMessageId).queue();
                    textChannel.deleteMessageById(messageId).queue();
                    Files.write(Paths.get("Username-ID_Lists.txt"), builder.toString().getBytes());
                    user.openPrivateChannel().complete().sendFile(new File("Username-ID_Lists.txt"), "Username-ID Lists.txt").queue();

                    eb.setTitle("사용자 이름 - ID 리스트를 DM으로 보냈어요!");
                    eb.setDescription("요청자 : " + user.getAsTag());
                    textChannel.sendMessage(eb.build()).queue();

                    Thread.sleep(10);
                    Files.delete(Paths.get("Username-ID_Lists.txt"));

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("echo") || args[0].equalsIgnoreCase("e")) {
                if (getHelpString.check(textChannel, args)) return;

                StringBuilder TTS = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    TTS.append(args[i]);
                    TTS.append(" ");
                }
                MessageBuilder mb = new MessageBuilder();
                mb.append(TTS.toString());
                mb.setTTS(true);
                textChannel.deleteMessageById(message.getId()).queue();
                textChannel.sendMessage(mb.build()).queue();
            } else if (args[0].equalsIgnoreCase("music") || args[0].equalsIgnoreCase("m")) {

                if (System.getProperty("os.arch").equalsIgnoreCase("arm")) {
                    eb.setTitle("죄송합니다...");
                    eb.setDescription("이 시스템은 음악 기능이 지원하지 않아요...");
                    eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                    return;
                }

                if (getHelpString.check(textChannel, args)) return;

                if (args[1].equalsIgnoreCase("join") || args[1].equalsIgnoreCase("j")) {

                    AudioManager audioManager = event.getGuild().getAudioManager();

                    if (audioManager.isConnected()) {
                        textChannel.sendMessage("이미 음성 채팅방에 연결 되어 있습니다.").queue();
                        return;
                    }

                    GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();

                    if (memberVoiceState != null && !memberVoiceState.inVoiceChannel()) {
                        textChannel.sendMessage(user.getAsMention() + "님, " + "음성 채팅방을 먼저 연결 하세요.").queue();
                        return;
                    }

                    VoiceChannel voiceChannel = null;
                    if (memberVoiceState != null) {
                        voiceChannel = memberVoiceState.getChannel();
                    }
                    Member selfMember = event.getGuild().getSelfMember();

                    if (voiceChannel != null && !selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                        textChannel.sendMessageFormat("%s 채널에 연결할 권한이 부족해요.", voiceChannel.getName()).queue();
                        return;
                    }

                    audioManager.openAudioConnection(voiceChannel);
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;
                    player.setVolume(80);
                    textChannel.sendMessage("음성 채팅방에 연결 했습니다.").queue();

                } else if (args[1].equalsIgnoreCase("leave") || args[1].equalsIgnoreCase("l")) {
                    AudioManager audioManager = event.getGuild().getAudioManager();

                    if (!audioManager.isConnected()) {
                        textChannel.sendMessage("음성 채팅방에 연결되어 있지 않습니다.").queue();
                        return;
                    }

                    VoiceChannel voiceChannel = audioManager.getConnectedChannel();

                    if (voiceChannel != null && !voiceChannel.getMembers().contains(event.getMember())) {
                        textChannel.sendMessage("당신은 음성 채팅방에 연결되어 있지 않으므로, 연결을 끊을 수 없습니다.").queue();
                        return;
                    }
                    audioManager.closeAudioConnection();
                    textChannel.sendMessage("음성 채팅방을 떠납니다.").queue();
                } else if (args[1].equalsIgnoreCase("play") || args[1].equalsIgnoreCase("p")) {

                    AudioManager audioManager = event.getGuild().getAudioManager();

                    if (!audioManager.isConnected()) {
                        textChannel.sendMessage("봇이 음성 채팅방에 연결되어 있지 않습니다.").queue();
                        return;
                    }

                    if(args[2].contains("mp3/"))
                    {
                        StringBuilder trimArgs = new StringBuilder();
                        for(String arg : args)  trimArgs.append(arg);

                        try {
                            String sql = "select * from saved_mp3 where filename = ?";
                            PreparedStatement prepareStatement = Main.con.prepareStatement(sql);

                            prepareStatement.setString(1, trimArgs.toString().replace("mpmp3/", ""));
                            ResultSet result = prepareStatement.executeQuery();

                            if (result.next()) {
                                Blob blob = result.getBlob("file");
                                InputStream inputStream = blob.getBinaryStream();
                                if(!Files.exists(Paths.get(trimArgs.toString().replace("mpmp3/", ""))))
                                {
                                    File targetFile = new File(trimArgs.toString().replace("mpmp3/", ""));
                                    FileUtils.copyInputStreamToFile(inputStream, targetFile);
                                }
                                args = ("m p http://localhost:" + Main.HTTP_PORT + "/" + trimArgs.toString().replace("mpmp3/", "")).split(" ");
                            }
                        }catch (Exception ignored){}
                        new PlayCommand(args, event, audioManager, trimArgs.toString().replace("mpmp3/", ""));
                    }else new PlayCommand(args, event, audioManager, null);

                } else if (args[1].equalsIgnoreCase("force_play") || args[1].equalsIgnoreCase("fp")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;

                    if (!user.getId().equals("345473282654470146")) {
                        eb.setTitle("봇 관리자가 아닙니다!");
                        eb.setDescription("당신은 이 봇의 관리자가 아니어서 강제 재생을 할 수 없습니다!");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }

                    if (player.getPlayingTrack() == null) {
                        textChannel.sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
                        return;
                    }
                    for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                        if (args[2].equalsIgnoreCase(audioTrack.getInfo().title) || audioTrack.getInfo().title.toLowerCase().contains(args[2].toLowerCase())) {
                            textChannel.sendMessage("기존 음악을 스킵하고, " + audioTrack.getInfo().title + "을 바로 재생합니다!").queue();
                            musicManager.scheduler.getQueue().remove(audioTrack);
                            musicManager.player.startTrack(audioTrack, false);
                            return;
                        }
                    }
                } else if (args[1].equalsIgnoreCase("stop")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

                    if (!user.getId().equals("345473282654470146")) {
                        eb.setTitle("봇 관리자가 아닙니다!");
                        eb.setDescription("당신은 이 봇의 관리자가 아니어서 대기열을 초기화 할 수 없습니다!");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                        return;
                    }

                    musicManager.scheduler.getQueue().clear();
                    musicManager.player.stopTrack();
                    musicManager.player.setPaused(false);

                    event.getChannel().sendMessage("음악을 멈추고 대기열을 비웁니다.").queue();
                } else if (args[1].equalsIgnoreCase("skip") || args[1].equalsIgnoreCase("s")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;
                    TrackScheduler scheduler = musicManager.scheduler;

                    if (player.getPlayingTrack() == null) {
                        textChannel.sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
                        return;
                    }

                    if (user.getId().equals("345473282654470146")) {
                        textChannel.sendMessage("당신은 이 봇의 관리자 입니다.\n" + player.getPlayingTrack().getInfo().title + "을 투표 없이 스킵합니다!").queue();
                        musicManager.scheduler.nextTrack();
                        return;
                    }

                    eb.setTitle(player.getPlayingTrack().getInfo().title + "를 스킵할까요?");
                    eb.setDescription(player.getPlayingTrack().getInfo().uri);
                    MessageAction messageAction = textChannel.sendMessage(eb.build());
                    Message embedMessage = messageAction.complete();
                    String embedMessageID = embedMessage.getId();
                    embedMessage.addReaction("U+2B55").complete();
                    embedMessage.addReaction("U+274C").complete();

                    try {
                        Thread.sleep(3500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    embedMessage = textChannel.retrieveMessageById(embedMessageID).complete();
                    List<MessageReaction> list = embedMessage.getReactions();
                    int O = 0, X = 0;
                    for (int i = 0; i < list.size(); i++) {
                        switch (i) {
                            case 0:
                                O = list.get(i).getCount() - 1;
                            case 1:
                                X = list.get(i).getCount() - 1;
                            default:
                                break;
                        }
                    }

                    textChannel.deleteMessageById(embedMessageID).complete();

                    if (O == X) textChannel.sendMessage("의견이 분분하네요! 노래는 스킵되지 않습니다!").queue();
                    else if (O > X) {
                        textChannel.sendMessage(player.getPlayingTrack().getInfo().title + "을 스킵합니다!").queue();
                        player.setPaused(true);
                        scheduler.nextTrack();
                        player.setPaused(false);
                    } else textChannel.sendMessage("스킵하지 말자는 의견이 많군요! 노래를 스킵하지 않습니다!").queue();

                } else if (args[1].equalsIgnoreCase("np")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;

                    if (player.getPlayingTrack() == null) {
                        textChannel.sendMessage("아무 것도 재생 되어 있지 않습니다.").queue();
                        return;
                    }
                    AudioTrackInfo info = player.getPlayingTrack().getInfo();

                    eb.setTitle(!player.isPaused() ? "재생 중: [" + info.title + "]" : "일시 정지됨: [" + info.title + "]");
                    eb.setDescription(player.isPaused() ? formatTime(player.getPlayingTrack().getPosition()) + " \u23F8 " + formatTime(player.getPlayingTrack().getDuration()) : formatTime(player.getPlayingTrack().getPosition()) + " ▶ " + formatTime(player.getPlayingTrack().getDuration()));
                    eb.setFooter(info.uri);
                    textChannel.sendMessage(eb.build()).queue();
                } else if (args[1].equalsIgnoreCase("pause")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;
                    if (!player.isPaused()) {
                        player.setPaused(true);
                        textChannel.sendMessage("플레이어를 일시정지 합니다.").queue();
                    } else textChannel.sendMessage("플레이어가 이미 일시정지 되어 있습니다.").queue();
                } else if (args[1].equalsIgnoreCase("resume")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;
                    if (player.isPaused()) {
                        player.setPaused(false);
                        textChannel.sendMessage("플레이어를 다시 재생 합니다.").queue();
                    } else textChannel.sendMessage("플레이어가 이미 재생되고 있습니다.").queue();
                } else if (args[1].equalsIgnoreCase("queue") || args[1].equalsIgnoreCase("q")) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;

                    if (args.length > 2) {
                        /*
                        if (args.length != 4) {
                            textChannel.sendMessage(getHelpString.main());
                            return;
                        }*/
                        if (args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("r")) {

                            if (!user.getId().equals("345473282654470146")) {
                                eb.setTitle("봇 관리자가 아닙니다!");
                                eb.setDescription("당신은 이 봇의 관리자가 아니어서 대기열를 제거 할 수 없습니다!");
                                eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                                textChannel.sendMessage(eb.build()).queue();
                                return;
                            }

                            if (Integer.parseInt(args[3]) == 0) {
                                eb.setTitle(args[3] + "번째 대기열은 제거할 수 없습니다!");
                                eb.setDescription("!!music skip을 사용하세요!");
                                eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                                textChannel.sendMessage(eb.build()).queue();
                                return;
                            }

                            if (player.getPlayingTrack() == null) {
                                eb.setTitle("음악 플레이리스트");
                                eb.setDescription("아무 것도 재생 되어 있지 않습니다!");
                                textChannel.sendMessage(eb.build()).queue();
                                return;
                            }
                            Object[] str = musicManager.scheduler.getQueue().toArray();
                            for (int i = 0; i < musicManager.scheduler.getQueue().size(); i++) {
                                AudioTrack audioTrack = (AudioTrack) str[i];
                                if (Integer.parseInt(args[3]) == (i + 1)) {
                                    musicManager.scheduler.getQueue().remove(audioTrack);
                                    eb.setTitle("음악 플레이리스트");
                                    eb.setDescription((i + 1) + "번째 대기열을 제거했습니다!");
                                    eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
                                    textChannel.sendMessage(eb.build()).queue();
                                    return;
                                }
                            }
                            eb.setTitle("음악 플레이리스트");
                            eb.setDescription("대기열을 제거하지 못했어요.. 대기열이 올바른지 확인하세요!");
                            eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }

                        if (args[2].equalsIgnoreCase("show") || args[2].equalsIgnoreCase("s")) {
                            if (player.getPlayingTrack() == null) {
                                eb.setTitle("음악 플레이리스트");
                                eb.setDescription("아무 것도 재생 되어 있지 않습니다!");
                                textChannel.sendMessage(eb.build()).queue();
                                return;
                            }

                            RestAction<Message> restAction = textChannel.sendMessage(0 + ". " + player.getPlayingTrack().getInfo().title + " [현재 재생 중]\n");
                            Message tmpMessage = restAction.complete();
                            String tmpMessageID = tmpMessage.getId();

                            int cnt = 0;
                            StringBuilder tmp = new StringBuilder();
                            if (args.length > 3) {
                                String[] SelectStr = new String[(musicManager.scheduler.getQueue().size() / 10) + 1];
                                for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                                    cnt++;
                                    tmp.append("> ").append(cnt).append(". ").append(audioTrack.getInfo().title).append("\n");
                                    if (cnt % 10 == 0) {
                                        SelectStr[(cnt / 10) - 1] = tmp.toString();
                                        tmp = new StringBuilder();
                                    }
                                }
                                SelectStr[(musicManager.scheduler.getQueue().size() / 10)] = tmp.toString();
                                try {
                                    textChannel.sendMessage(SelectStr[Integer.parseInt(args[3]) - 1]).queue();
                                    textChannel.sendMessage(args[3] + " / " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + " 페이지").queue();
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    textChannel.deleteMessageById(tmpMessageID).complete();
                                    eb.setTitle("오류! " + args[3] + "페이지는 존재하지 않습니다!");
                                    eb.setFooter(user.getAsTag(), user.getAvatarUrl());
                                    textChannel.sendMessage(eb.build()).queue();
                                }
                            } else {
                                for (AudioTrack audioTrack : musicManager.scheduler.getQueue()) {
                                    cnt++;
                                    tmp.append("> ").append(cnt).append(". ").append(audioTrack.getInfo().title).append("\n");
                                    if (cnt % 10 == 0) {
                                        textChannel.sendMessage(tmp.toString()).queue();
                                        textChannel.sendMessage("현재 플레이어의 재생 목록은 " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + "개의 페이지가 있습니다.").queue();
                                        tmp = new StringBuilder();
                                        break;
                                    }
                                }
                                if (tmp.toString().equals("")) return;
                                textChannel.sendMessage(tmp.toString()).queue();
                                textChannel.sendMessage("현재 플레이어의 재생 목록은 " + ((musicManager.scheduler.getQueue().size() / 10) + 1) + "개의 페이지가 있습니다.").queue();
                            }
                        }
                    }
                } else if (args[1].equalsIgnoreCase("volume") | args[1].equalsIgnoreCase("v")) {
                    if (args.length != 3) {
                        textChannel.sendMessage(getHelpString.main()).queue();
                        return;
                    }
                    PlayerManager playerManager = PlayerManager.getInstance();
                    GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
                    AudioPlayer player = musicManager.player;
                    if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 101) {
                        player.setVolume(Integer.parseInt(args[2]));
                        textChannel.sendMessage("플레이어의 볼륨이 " + Integer.parseInt(args[2]) + "으로 설정되었습니다.").queue();
                    } else textChannel.sendMessage("볼륨이 너무 크거나 작습니다.").queue();
                }
            } else if (args[0].equalsIgnoreCase("sysInfo")) {

                eb.setTitle("잠시만 기다려 주세요! 프로세서의 개수에 따라 시간이 걸립니다!");
                eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                RestAction<Message> restAction = textChannel.sendMessage(eb.build());
                Message noticeMessage = restAction.complete();
                String noticeMessageId = noticeMessage.getId();

                SystemInfo systemInfo = new SystemInfo();
                HardwareAbstractionLayer hal = systemInfo.getHardware();

                String OS = System.getProperty("os.name");
                String Temp = getCPUUsage.temp();

                if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                Runtime
                                        .getRuntime()
                                        .exec("uname -s -r")
                                        .getInputStream(), StandardCharsets.UTF_8));
                        OS = br.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (System.getProperty("os.arch").equalsIgnoreCase("arm")) {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                Runtime
                                        .getRuntime()
                                        .exec("cat /sys/class/thermal/thermal_zone0/temp")
                                        .getInputStream(), StandardCharsets.UTF_8));
                        Temp = br.readLine();
                        Temp = String.format("%.1f", Double.parseDouble(Temp) / 1000.0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                textChannel.sendMessage("시스템 정보: \n" + "> CPU - " + hal.getProcessor().getName().replace("CPU ", "") +
                        ", Temp: " + Temp + "°C, " + getCPUUsage.main() + "%" + "\n" +
                        "> Memory - MaxHeapSize: " + Runtime.getRuntime().maxMemory() / 1048576 + "M,  " +
                        "AllocatedHeapSize: " + Runtime.getRuntime().totalMemory() / 1048576 + "M,  " +
                        "FreeHeapSize: " + Runtime.getRuntime().freeMemory() / 1048576 + "M\n> \n" +
                        "> OS - " + OS + ", " + System.getProperty("os.arch") + "\n" +
                        "> JVM - " + System.getProperty("java.vm.name") + " (build " + System.getProperty("java.runtime.version") + ")").queue();
                textChannel.deleteMessageById(noticeMessageId).queue();

            } else if (args[0].equalsIgnoreCase("shutdown")) {
                if (user.getId().equals("345473282654470146")) {
                    AudioManager audioManager = event.getGuild().getAudioManager();
                    audioManager.closeAudioConnection();
                    SQL.finSQLConnection(Main.con);
                    eb.setTitle("봇의 전원이 꺼집니다!");
                    textChannel.sendMessage(eb.build()).queue();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        eb.setTitle("봇이 응답하지 않습니다!");
                        eb.setDescription("무언가 문제가 생긴거 같군요..");
                        textChannel.sendMessage(eb.build()).queue();
                    }
                    event.getJDA().shutdownNow();
                    System.exit(0);
                } else {
                    eb.setTitle("이봐... 너는 내가 아니야..");
                    eb.setDescription("어이어이.. 돌아가라구..");
                    eb.setFooter("요청자: " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                }
            } else if (args[0].equalsIgnoreCase("ping")) {
                event.getChannel().sendMessage("Pong!").queue((pingMessage) ->
                        pingMessage.editMessageFormat("Discord Gateway Responsed in %sms", event.getJDA().getGatewayPing()).queue()
                );
            } else if (args[0].equalsIgnoreCase("mp3")) {
                try {
                    if (args[1].equals("upload")) {
                        if (message.getAttachments().size() > 1) {
                            textChannel.sendMessage("파일을 하나씩만 보내주세요!").queue();
                            return;
                        }

                        Message.Attachment attachment;
                        try {
                            attachment = message.getAttachments().get(0);
                        } catch (IndexOutOfBoundsException e) {
                            eb.setTitle("오류!");
                            eb.setDescription("```\n음악 파일이 필요합니다!\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }

                        File file = new File(attachment.getFileName());
                        CompletableFuture<File> fileCompletableFuture = attachment.downloadToFile();
                        try {
                            file = fileCompletableFuture.get();
                        } catch (java.util.concurrent.ExecutionException | InterruptedException ignored) {
                        }

                        byte[] fileByte = null;
                        try {
                            fileByte = Files.readAllBytes(Paths.get(file.getPath()));
                        } catch (IOException e) {
                            eb.setTitle("오류!");
                            eb.setDescription("```\n파일을 받을 수 없습니다!\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < 3; i++) sb.append(String.format("%02x ", fileByte[i] & 0xff));
                        sb.deleteCharAt(sb.length() - 1);

                        if (sb.toString().equals("49 44 33")) {
                            textChannel.sendMessage("MP3 파일입니다!").queue();
                            try {
                                int b;
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                FileInputStream fileInputStream = new FileInputStream(file);

                                while ((b = fileInputStream.read()) != -1) byteArrayOutputStream.write(b);
                                fileInputStream.close();
                                byteArrayOutputStream.close();

                                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                                PreparedStatement prepareStatement = Main.con.prepareStatement("insert into saved_mp3 values (?, ?)");
                                prepareStatement.setString(1, file.getName());
                                prepareStatement.setBinaryStream(2, byteArrayInputStream, byteArrayOutputStream.size());
                                prepareStatement.executeUpdate();

                                textChannel.sendMessage("업로드 성공!").queue();
                                prepareStatement.close();
                            } catch (Exception e) {
                                textChannel.sendMessage(e.getMessage()).queue();
                            }
                        } else {
                            eb.setTitle("오류!");
                            eb.setDescription("```Java\nMP3 파일이 아닙니다! " + "(파일 헤더: \"" + sb.toString() + "\")\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                        }
                        file.delete();
                    } else if (args[1].equalsIgnoreCase("download") || args[1].equalsIgnoreCase("down")) {
                        boolean isFileExist = false;
                        String mp3Name = "";
                        //if(!user.getId().equals("345473282654470146")) textChannel.sendMessage("준비중입니다!").queue();
                        if (args.length < 3) {
                            eb.setTitle("오류!");
                            eb.setDescription("```Java\nMP3 파일의 번호를 입력하지 않았습니다!\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }

                        String[] mp3List = SQL.getSQLData(Main.con, "saved_mp3", "filename", event);

                        for (int i = 0; i < mp3List.length; i++) mp3List[i] = ((i + 1) + ". " + mp3List[i]);

                        for (String SQLmp3Name : mp3List) {
                            if (args[2].equals(SQLmp3Name.substring(0, 1))) {
                                isFileExist = true;
                                mp3Name = SQLmp3Name.substring(2);
                                textChannel.sendMessage("Found MP3 Music File: " + mp3Name).queue();
                                break;
                            }
                        }

                        if (!isFileExist) {
                            eb.setTitle("오류!");
                            eb.setDescription("```Java\n파일 번호에 일치하는 MP3 파일이 없습니다!\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                            return;
                        }

                        RestAction<Message> messageAction = textChannel.sendMessage("전송 중....");
                        String messageId = messageAction.complete().getId();

                        try {
                            String tmpPath = System.getProperty("java.io.tmpdir");
                            String sql = "select * from saved_mp3 where filename = ?";
                            PreparedStatement prepareStatement = Main.con.prepareStatement(sql);

                            prepareStatement.setString(1, mp3Name.substring(1));
                            ResultSet result = prepareStatement.executeQuery();

                            if (result.next()) {
                                Blob blob = result.getBlob("file");
                                InputStream inputStream = blob.getBinaryStream();

                                if (event.getJDA().getSelfUser().getAllowedFileSize() <= blob.length()) {
                                    eb.setTitle("경고!");
                                    eb.setDescription("```Java\n전송하려는 파일이 너무 큽니다! (분할 압축 후 전송)\n```");
                                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                                    textChannel.editMessageById(messageId, eb.build()).queue();

                                    Thread.sleep(500);

                                    ZipParameters zipParameters = new ZipParameters();
                                    zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
                                    zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
                                    zipParameters.setFileNameInZip(mp3Name);

                                    ZipFile mp3Zip = new ZipFile(tmpPath + mp3Name.replace(".mp3", ".zip"));

                                    File mp3File = new File(tmpPath + System.currentTimeMillis() + "");
                                    FileUtils.copyInputStreamToFile(inputStream, mp3File);
                                    inputStream.close();

                                    mp3Zip.createSplitZipFile(Collections.singletonList(mp3File), zipParameters, true,8388300);
                                    for(File splitMP3ZipFile : mp3Zip.getSplitZipFiles())
                                    {
                                        textChannel.sendFile(splitMP3ZipFile).complete();
                                        splitMP3ZipFile.delete();
                                    }
                                    textChannel.deleteMessageById(messageId).queue();
                                    mp3File.delete();
                                    return;
                                }

                                textChannel.sendFile(inputStream, mp3Name).complete();
                                textChannel.deleteMessageById(messageId).queue();
                                inputStream.close();
                            }

                            result.close();
                            prepareStatement.close();

                        } catch (SQLException | IOException | InterruptedException e) {
                            textChannel.sendMessage(e.getMessage()).queue();
                            textChannel.deleteMessageById(messageId).queue();
                        }

                    } else if (args[1].equals("list")) {
                        String[] mp3List = SQL.getSQLData(Main.con, "saved_mp3", "filename", event);
                        if (mp3List.length == 0) {
                            eb.setTitle("Music file list: ");
                            eb.setDescription("```\n" + "DB에 저장된 음악 파일이 없습니다!" + "\n```");
                            eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                            textChannel.sendMessage(eb.build()).queue();
                        }
                        int cnt = 0;
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String mp3Name : mp3List) {
                            stringBuilder.append(++cnt).append(": ").append(mp3Name).append("\n");
                        }
                        eb.setTitle("Music file list: ");
                        eb.setDescription("```\n" + stringBuilder.toString() + "\n```");
                        eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                        textChannel.sendMessage(eb.build()).queue();
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    eb.setTitle("사용법");
                    eb.setDescription("```\n" + "!!mp3 [upload / download / list]\n\n" +
                            "[upload] - 파일 붙여서 보내기\n" +
                            "[download] [num] - MP3 파일의 번호를 입력합니다. (공사 완료)\n" +
                            "[list] DB에 저장된 음악 파일 번호와 이름을 출력합니다.\n\n" +
                            "!!m p mp3/[filename]\n" +
                            "[filename] - 재생할 파일 이름을 입력헙나다. (확장자 포함)\n" + "\n```");
                    eb.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());
                    textChannel.sendMessage(eb.build()).queue();
                }
            }
            else if(args[0].equalsIgnoreCase("BringMeThanos"))
            {
                eb.setTitle("BRING ME THANOS!!!!!!!!!!!!!!!!!!!!!");
                eb.setDescription("```diff\n--YOUR TEXT CHANEL HAS BEEN THANOSED.\n```");
                textChannel.sendMessage(eb.build()).queue();
            }
        }
    }

    public static String CheckState(String str) {
        if (str.contains("open|filtered")) return "open|filtered";
        if (str.contains("open")) return "open";
        if (str.contains("filtered")) return "filtered";
        if (str.contains("closed")) return "closed";
        if (str.contains("unfiltered")) return "unfiltered";

        return "";
    }

    public boolean areYouCrazy(String arg, String[] Blocklists) {
        for (String blocklist : Blocklists) if (arg.contains(blocklist)) return true;
        return false;
    }

    public void printErrorArgs(User user, EmbedBuilder eb, TextChannel textChannel) {
        eb.setTitle("인자 오류!");
        eb.setDescription("오타가 발생했네요..");
        eb.setFooter("요청자 : " + user.getAsTag());
        textChannel.sendMessage(eb.build()).queue();
    }

    public static boolean isPortSpecific(String arg) {
        arg = arg.replace("-", "");

        for (int i = 0; i < arg.length(); i++) if (!Character.isDigit(arg.charAt(i))) return false;

        return true;
    }

    public String formatTime(long timeInMillis) {

        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, (minutes > 59) ? minutes % 60 : minutes, seconds);

    }
}
