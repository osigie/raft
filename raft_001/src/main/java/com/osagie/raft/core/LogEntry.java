package src.main.java.com.osagie.raft.core;

public class LogEntry {
  private final int term;
  private final int index;
  private final String command;

  public LogEntry(int term, int index, String command) {
    this.term = term;
      this.index = index;
      this.command = command;
  }

  public int getTerm() {
    return term;
  }

  public int getIndex() {
    return index;}

  public String getCommand() {
    return command;
  }

  @Override
  public String toString() {
    return "LogEntry{" +
        "term=" + term +
        ", command='" + command + '\'' +
        '}';
  }
}
