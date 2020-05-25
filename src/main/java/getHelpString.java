import net.dv8tion.jda.api.entities.TextChannel;

public class getHelpString {

    public static String main() {

        return "> ZENITSU BOT v4.5\n" +

                "```css\n" +
                "!!cc [line]\n" +
                "- 채팅을 깔끔하게 청소합니다.\n" +
                "- 2줄부터 99줄 까지만 청소가 가능" +
                "합니다.\n" +
                "\n" +
                "\n" +
                "!!port-scanner [addr..] <all> or <specific ports>\n" +
                "- [addr]에 열러있는 포트를 스캔해요.\n" +
                "- <specific ports>에는 특정한 단일포트(ex.25565), 다중포트(ex.25565-25580)를 입력해요.\n" +
                "- 스캔 시에 생기는 모든 법적 책임은 사용자한테 있습니다!\n" +
                "\n" +
                "\n" +
                "!!getNamesByID\n" +
                "- 암호를 입력해야 해요!\n" +
                "- 암호가 일치하면 port-scanner의 로그를 확인하는데 필요한 파일을 줘요.\n" +
                "- *로그를 볼 수 있는 사용자만 사용이 가능합니다.*\n" +
                "\n" +
                "\n" +
                "!!echo [args]\n" +
                "- [args]를 TTS로 말해요!\n" +
                "\n" +
                "\n" +
                "!!port-scanner blocklists [add/remove/list] <addr..>\n" +
                "- 포트 스캔 블록리스트를 관리해요.\n" +
                "- *관리자만 사용이 가능합니다.*\n" +
                "\n" +
                "\n" +
                "!!music [play]" + "<URL> or <Video Name>\n" +
                "- [play] 봇이 음성 채팅방에서 유튜브를 틀어요.\n" +
                "- <URL or Video Name>에서 URL 또는 동영상 이름으로 유튜브를 틀 수 있어요.\n"+
                "※ 주의! 동영상 이름으로 유튜브를 틀 때 짧은 시간에 너무 많은 요청을 하면\n  Google API에서 이 봇을 차단하여 동영상 검색 기능을 사용할 수 없게 되요!\n\n\n" +
                "!!music [join/leave/pause/resume/volume/playlists/skip/stop/np]\n" +
                "- [join] 봇이 자신이 연결 되어있는 음성 채팅방에 들어와요.\n" +
                "- [leave] 봇이 자신이 연결 되어있는 음성 채팅방에 떠나요.\n" +
                "- [pause] 봇이 현재 재생하고 있는 유튜브를 일시정지 해요.\n" +
                "- [resume] 봇이 현재 일시정지된 유튜브를 다시 재생해요. \n" +
                "- [volume] 봇의 소리를 키우거나 줄여요.(기본 20, 1~100 가능)\n" +
                "- [playlists] 봇의 재생 목록을 보여줘요.\n" +
                "- [playlists] [remove] <num> 봇의 재생 목록중 하나를 골라 대기열에서 삭제해요.\n" +
                "- [skip] 봇이 현재 재생하고 있는 유튜브를 건너띄어요.\n" +
                "- [stop] 봇이 현재 재생하고 있는 유튜브를 건너띄우고 모든 대기열을 삭제해요, \n" +
                "- [np] 현재 봇이 재생하고 있는 유튜브와 재생 상태를 알려줘요.\n"+
                "\n" +
                "\n" +
                "!!sysInfo\n" +
                "- 봇의 시스템 정보를 출력해요!\n" +
                "```";
    }

    public static boolean check(TextChannel textChannel, String[] args)
    {
        if(args.length < 2)
        {
            textChannel.sendMessage(getHelpString.main()).queue();
            return true;
        }
        return false;
    }
}
