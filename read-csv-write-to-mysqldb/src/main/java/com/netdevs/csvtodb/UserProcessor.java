package com.netdevs.csvtodb;

import org.springframework.batch.item.ItemProcessor;


public class UserProcessor implements ItemProcessor<User, User>{

	@Override
	public User process(User item) throws Exception {
		
		// TODO Auto-generated method stub
		return  new User(item.getName(), item.getEmail());
	}

}
