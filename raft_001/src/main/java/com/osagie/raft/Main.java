package src.main.java.com.osagie.raft;

import src.main.java.com.osagie.raft.core.RaftNode;
import src.main.java.com.osagie.raft.transport.InMemoryTransport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
  public static void main(String[] args) throws InterruptedException {

    System.out.println("Raft implementation starting...");
    // Create shared transport
    InMemoryTransport transport = new InMemoryTransport();

    // Create 5 nodes
    Set<String> allNodes = Set.of("node1", "node2", "node3", "node4", "node5");
    List<RaftNode> nodes = new ArrayList<>();

    for (String nodeId : allNodes) {
      Set<String> peers = new HashSet<>(allNodes);
      peers.remove(nodeId); // Remove self from peers

      RaftNode node = new RaftNode(nodeId, peers, transport);
      nodes.add(node);
    }

    // Start all nodes
    System.out.println("Starting Raft cluster with 5 nodes...\n");
    for (RaftNode node : nodes) {
      node.start();
    }

    // Let it run for 10 seconds
    Thread.sleep(10000);

    // Print final states
    System.out.println("\n=== FINAL STATES ===");
    for (RaftNode node : nodes) {
      System.out.println("Node " + node.getNodeId() + ": " +
              node.getState() + " (term " + node.getCurrentTerm() + ")");
    }
  }
}
