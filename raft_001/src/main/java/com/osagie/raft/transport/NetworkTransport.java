package src.main.java.com.osagie.raft.transport;

import src.main.java.com.osagie.raft.core.LogEntry;
import src.main.java.com.osagie.raft.core.RaftNode;

import java.util.List;

public interface NetworkTransport {
    void registerNode(RaftNode node);
    void sendRequestVote(String nodeId, int term, String candidateId, int lastLogIndex, int lastLogTerm);
    void sendVoteResponse(String nodeId, int term, boolean voteGranted);
    void sendAppendEntries(String nodeId, int term, String leaderId, int prevLogIndex, int prevLogTerm,
                           List<LogEntry> entries, int leaderCommit);
    void sendAppendEntriesResponse(String nodeId, int term, boolean success);
}