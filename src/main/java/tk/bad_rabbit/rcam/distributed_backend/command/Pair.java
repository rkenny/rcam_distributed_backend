package tk.bad_rabbit.rcam.distributed_backend.command;

public class Pair<FirstType, SecondType> {
  private FirstType ackNumber;
  private SecondType returnCode;
  
  public Pair(FirstType ackNumber, SecondType returnCode) {
    this.ackNumber = ackNumber;
    this.returnCode = returnCode;
  }
  
  public FirstType getLeft() {
    return ackNumber;
  }
  
  public SecondType getRight() {
    return returnCode;
  }
  
  @Override
  public int hashCode() {
    return ackNumber.hashCode() ^ returnCode.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Pair)) return false;
    return (this.ackNumber.equals(((Pair) o).getLeft())) && (this.returnCode.equals(((Pair) o).getRight()));
  }
}
