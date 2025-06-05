package src.main.java.com.osagie.raft.core;

import src.main.java.com.osagie.raft.transport.NetworkTransport;

import java.util.*;

public class RaftNode {
    private int currentTerm = 0;
    private RaftState state = RaftState.FOLLOWER;
    private String votedFor = null;
    private final String nodeId;
    private final Set<String> peerIds;
    private final NetworkTransport transport;
    private List<LogEntry> log = new ArrayList<>();


    //election state
    private int votesReceived = 0;
    private Timer electionTimer;
    private final Random random = new Random();


    public RaftNode(String nodeId, Set<String> peerIds, NetworkTransport transport) {
        this.nodeId = nodeId;
        this.peerIds = peerIds;
        this.transport = transport;
    }

    public void start(){
        transport.registerNode(this);
        startElectionTimeout();
        System.out.println("RaftNode " + nodeId + " started in state: " + state);
    }

    private void startElectionTimeout() {
        if(electionTimer != null) {
            electionTimer.cancel();
        }

        electionTimer = new Timer();
        int timeout = 150 + random.nextInt(150); // Random timeout between 150 and 300 ms
        electionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state != RaftState.LEADER) {
                    startElection();
                }
            }
        }, timeout);
    }

   private void startElection(){
        state = RaftState.CANDIDATE;
        currentTerm++;
        votedFor = nodeId;
        votesReceived = 1; // Vote for self

       System.out.println("Node " + nodeId + " starting election for term " + currentTerm);
       startElectionTimeout();

       //send vote request to all peers

       for(String peerId : peerIds) {
           transport.sendRequestVote(peerId, currentTerm, nodeId, getLastLogIndex(), getLastLogTerm());
       }
   }

   public void handleRequestVote(int term, String candidateId, int lastLogIndex, int lastLogTerm) {
       System.out.println("Node " + nodeId + " received vote request from " + candidateId + " for term " + term);

       boolean voteGranted = false;

        if (term > currentTerm) {
            currentTerm = term;
            state = RaftState.FOLLOWER;
            votedFor = null; // Reset vote
        }


        if(term >= currentTerm &&(votedFor == null || votedFor.equals(candidateId))){
            //check if candidate's log is at least as up-to-date as this node's log
            if (lastLogTerm > getLastLogTerm() || (lastLogTerm == getLastLogTerm() && lastLogIndex >= getLastLogIndex())) {
                votedFor = candidateId;
                 voteGranted = true;
                startElectionTimeout();
            }
        }


       transport.sendVoteResponse(candidateId, currentTerm, false);
       System.out.println("Node " + nodeId + " " + (voteGranted ? "granted" : "denied") + " vote to " + candidateId + " for term " + term);
    }

    public void handleVoteResponse(int term, boolean voteGranted) {
        if(state != RaftState.CANDIDATE || term < currentTerm) {
            return; // Ignore responses if not a candidate or term is outdated
        }

        if(voteGranted){
            votesReceived++;
            int majority = (peerIds.size() / 2) + 1;

            if(votesReceived >= majority) {
                becomeLeader();
            }
        }


    }

    private void becomeLeader() {
state = RaftState.CANDIDATE;
electionTimer.cancel();
        System.out.println("Node " + nodeId + " became leader for term " + currentTerm);

        startHeartbeat();
    }

    private void startHeartbeat() {
        Timer heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (state == RaftState.LEADER) {
                    sendHeartbeats();
                } else {
                    cancel();
                }
            }

        }, 0, 100); // Send heartbeat every 100 ms
    }


    private void sendHeartbeats() {
        for(String peerId : peerIds) {
            transport.sendAppendEntries(peerId, currentTerm, nodeId, getLastLogIndex(), getLastLogTerm(), Collections.emptyList(), 0);
        }
    }

    public void handleAppendEntries(int term, String leaderId, int prevLogIndex, int prevLogTerm, List<LogEntry> entries, int leaderCommit) {
        System.out.println("Node " + nodeId + " received append entries from " + leaderId + " for term " + term);

        boolean success = false;


        if (term > currentTerm) {
            currentTerm = term;
            votedFor = null; // Reset vote
        }

        if(term >= currentTerm){
            state = RaftState.FOLLOWER;
            startElectionTimeout();
            success = true;
            System.out.println("Node " + nodeId + " accepted append entries from " + leaderId + " for term " + term);
        }

        transport.sendAppendEntriesResponse(leaderId, currentTerm, success );


    }

    public  void handleAppendEntriesResponse(int term, boolean success) {
        if (!success) {
            System.out.println("Node " + nodeId + " received failed append entries response");
        }
        }

    private int getLastLogIndex() {
        return log.size() - 1;
    }

    private int getLastLogTerm() {
        return log.isEmpty() ? 0 : log.get(log.size() - 1).getTerm();
    }

    public String getNodeId() { return nodeId; }
    public RaftState getState() { return state; }
    public int getCurrentTerm() { return currentTerm; }
}
