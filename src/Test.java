import java.util.Random;

public class Test {
	public static void main(String[] args) {
		int numberOfAcc = 10;
		SavingAccount[] s = new SavingAccount[numberOfAcc];
		Thread[] threads = new Thread[numberOfAcc];
		Random r = new Random();

		// create accounts with random initial balance between 0 and 200
		for(int i=0;i<numberOfAcc;i++){
			s[i]=new SavingAccount(r.nextInt(200),"Account:"+(i+1));
			System.out.println(s[i].getName()+" - Balance:"+s[i].getBalance());
		}

		// transfer $100 from each account to any other random account
		for(int i=0; i<numberOfAcc;i++){
			int idx = r.nextInt(numberOfAcc);
			if(idx==i)
				idx+=1;
			threads[i]=new Thread(new transferRunnable(s[i], s[idx%numberOfAcc], 100, r.nextBoolean()));
			threads[i].start();
		}

		// wait for an hour 
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n\nAfter One Hours wait.");

		// deposit 1000$ to all the accounts
		Thread[] bossThreads = new Thread[numberOfAcc];
		for(int i=0; i<numberOfAcc; i++){
			bossThreads[i] = new Thread(new depositRunnable(s[i],1000));
			bossThreads[i].start();
		}
		
	}
}

class transferRunnable implements Runnable{

	SavingAccount fromAccount, toAccount;
	int amount = 0;
	boolean preferredTransfer = false;
	transferRunnable(SavingAccount fromAccount,SavingAccount toAccount, int k, boolean preferredTransfer){
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = k;
		this.preferredTransfer = preferredTransfer;
	}

	transferRunnable(SavingAccount fromAccount,SavingAccount toAccount, int k){
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = k;
	}

	@Override
	public void run() {
		try {
			System.out.println("Amount transfer started from "+fromAccount.getName()+" to "+toAccount.getName());
			toAccount.transfer(amount, fromAccount, preferredTransfer);
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception caught!");
			e.printStackTrace();
		}	
	}
}

class depositRunnable implements Runnable{
	SavingAccount account;
	int amount = 0;

	depositRunnable(SavingAccount account, int k){
		this.account = account;
		this.amount = k;
	}

	@Override
	public void run() {
		account.deposit(amount);
	}

}
