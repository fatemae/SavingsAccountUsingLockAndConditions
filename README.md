# SavingAccountUsingConditions

A savings account object holds a nonnegative balance, and provides deposit(k) and withdraw(k) methods, where deposit(k) adds k to the balance, and withdraw(k) subtracts k, if the balance is at least k, and otherwise blocks until the balance becomes k or greater. 

- Implement this savings account using locks and conditions. 

- Now suppose there are two kinds of withdrawals: ordinary and preferred. Devise an implementation that ensures that no ordinary withdrawal occurs if there is a preferred withdrawal waiting to occur. 

- Now add a transfer() method that transfers a sum from one account to another: 
> void transfer(int k, Account reserve) { 
	lock.lock();
	try {        
		reserve.withdraw(k);deposit(k); 
	} finally { 
       lock.unlock();    
	}
} 