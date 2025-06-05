package src.main.java.com.osagie.raft.transport;

import src.main.java.com.osagie.raft.core.LogEntry;
import src.main.java.com.osagie.raft.core.RaftNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTransport implements NetworkTransport{
    private final Map<String, RaftNode> nodes = new ConcurrentHashMap<>();

    @Override
    public void registerNode(RaftNode node) {
nodes.put(node.getNodeId(), node);
    }

    @Override
    public void sendRequestVote(String nodeId, int term, String candidateId, int lastLogIndex, int lastLogTerm) {
        RaftNode targetNode = nodes.get(nodeId);
        if (targetNode != null) {
            //simulate network delay
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    targetNode.handleRequestVote(term, candidateId, lastLogIndex, lastLogTerm);
                }
            }, 10 + new Random().nextInt(20) // random delay between 10 and 30 ms);
            );
        }

    }

    @Override
    public void sendVoteResponse(String nodeId, int term, boolean voteGranted) {
RaftNode targetNode = nodes.get(nodeId);
        if (targetNode != null) {
            //simulate network delay
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    targetNode.handleVoteResponse(term, voteGranted);
                }
            }, 10 + new Random().nextInt(20)); // random delay between 10 and 30 ms
        }
    }

    @Override
    public void sendAppendEntries(String nodeId, int term, String leaderId, int prevLogIndex, int prevLogTerm, List<LogEntry> entries, int leaderCommit) {
RaftNode targetNode = nodes.get(nodeId);
        if (targetNode != null) {
            //simulate network delay
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    targetNode.handleAppendEntries(term, leaderId, prevLogIndex, prevLogTerm, entries, leaderCommit);
                }
            }, 10 + new Random().nextInt(20)); // random delay between 10 and 30 ms
        }
    }

    @Override
    public void sendAppendEntriesResponse(String nodeId, int term, boolean success) {
RaftNode targetNode = nodes.get(nodeId);
        if (targetNode != null) {
            //simulate network delay
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    targetNode.handleAppendEntriesResponse(term, success);
                }
            }, 10 + new Random().nextInt(20)); // random delay between 10 and 30 ms
        }
    }
}
