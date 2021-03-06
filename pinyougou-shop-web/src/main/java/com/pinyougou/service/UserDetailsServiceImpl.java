package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	private SellerService sellerService;

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			TbSeller seller = sellerService.findOne(username);
			List<SimpleGrantedAuthority> authorities=new ArrayList<SimpleGrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
			if (seller!=null) {
				if (seller.getStatus().equals("1")) {
					return new User(username, seller.getPassword(), authorities);
					
				}else {
					return null;
				}

			}else {
				return null;
			}
		
	}

}
