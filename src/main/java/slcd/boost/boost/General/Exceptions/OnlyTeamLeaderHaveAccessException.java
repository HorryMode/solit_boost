package slcd.boost.boost.General.Exceptions;

public class OnlyTeamLeaderHaveAccessException extends RuntimeException{

    private static final String NAME = "ONLY_TEAM_LEADER_HAVE_ACCESS";

    public OnlyTeamLeaderHaveAccessException (String message){
        super(message);
    }
}
