package src.main.java.com.osagie.raft.core;

public enum RaftState {
    FOLLOWER,
    CANDIDATE,
    LEADER;

    @Override
    public String toString() {
        return "RaftState{" +
                "name='" + name() + '\'' +
                '}';
    }
}
