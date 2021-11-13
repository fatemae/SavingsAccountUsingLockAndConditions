import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingAccount {
    // Lock for transfer(withdraw+deposit)
    final Lock lock = new ReentrantLock();
    // lock for Withdraw and deposit sub operations
    final Lock WDLock = new ReentrantLock();
    private int balance=0;
    private String name;
    private Condition ordinaryWithdrawalCondition = WDLock.newCondition();
    private Condition preferredWithdrawalCondition = WDLock.newCondition();
    // to keep track of number of threads in preferred waiting
    static int preferredWaitingThread=0;
    

    public SavingAccount(int balance, String name){
        this.balance=balance;
        this.name=name;
    }

    void transfer(int k, SavingAccount reserve, boolean preferredTransfer) throws InterruptedException { 
        lock.lock();
        try { 
            reserve.withdraw(k, preferredTransfer);
            deposit(k); 
            System.out.println("Successful $100 transfer from "+reserve.name+" to "+this.name);
        } finally { 
            lock.unlock();    
		}
	} 

    void deposit(int k) {
        WDLock.lock();
        try{
            this.balance += k;
            System.out.println("Thread "+Thread.currentThread().getName()+"--"+this.name+" has a new balance "+this.balance);           
            System.out.println(preferredWaitingThread);
            // If there is any preferred waiting thread after deposit, notify them 
            // else notify the ordinary threads
            if(preferredWaitingThread!=0){
                // preferredWaitingThread=0;
                preferredWithdrawalCondition.signalAll();
            }else{
                ordinaryWithdrawalCondition.signalAll();
            }

        }finally{
            WDLock.unlock();
        }

    }

    private void withdraw(int k, boolean preferredWithdrawal) throws InterruptedException {
        WDLock.lock();
        try{
            if(preferredWithdrawal){
                // If its a preferred withdrawal go ahead and check if there is enough balance to withdraw or wait
                preferredWaitingThread++;
                while(this.balance < k){
                    System.out.println("Withdraw from "+getName()+" with balance "+getBalance()+" is in preferred waiting");
                    preferredWithdrawalCondition.await();
                }
                preferredWaitingThread--;
                this.balance -= k;
                System.out.println("Thread "+Thread.currentThread().getName()+"--"+this.name+" has a new balance "+this.balance);
                // After withdrawal check if there is a preferred thread waiting gignal them else signal the ordinary threads to go ahead
                if(preferredWaitingThread!=0){
                    // preferredWaitingThread=0;
                    preferredWithdrawalCondition.signalAll();
                }else{
                    ordinaryWithdrawalCondition.signalAll();
                }
                
            }else{
                // If its not a preferred withdrawal check first if there is a preferred withdrawal waiting
                while(preferredWaitingThread!=0){
                    // preferredWaitingThread=0;
                    System.out.println("Withdraw from "+getName()+" with balance "+getBalance()+" is in ordinary waiting");
                    
                    ordinaryWithdrawalCondition.await();
                    preferredWithdrawalCondition.signalAll();
                }
                // Check for enough balance or wait
                while(this.balance<k){
                    System.out.println("Withdraw from "+getName()+" with balance "+getBalance()+" is in ordinary waiting");
                    ordinaryWithdrawalCondition.await();
                }
                this.balance -= k;
                System.out.println("Thread "+Thread.currentThread().getName()+"--"+this.name+" has a new balance "+this.balance);
                // Notify the the preferred or ordinary thread to go ahead
                if(preferredWaitingThread!=0){
                    // preferredWaitingThread=0;
                    preferredWithdrawalCondition.signalAll();
                }else{
                    ordinaryWithdrawalCondition.signalAll();
                }
            }
        }finally{
            WDLock.unlock();
        }
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
