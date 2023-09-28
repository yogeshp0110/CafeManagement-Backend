package com.inn.cafe.serviceImpl;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtils;
import com.inn.cafe.contents.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.model.User;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.UserWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	JwtFilter jwtFilter;
	
//	@Autowired
//	MailUtils mailUtils;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
    log.info("Inside signup {}" ,requestMap);
	try {
	if(validateSignUpMap(requestMap)) {
		User user=userDao.findByEmailId(requestMap.get("email"));
		
		if(Objects.isNull(user)) {
			userDao.save(getUserFromMap(requestMap));
			return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
			
		}else {
			return CafeUtils.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
		}
	}
	else {
		return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
	}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

}
	
	private boolean validateSignUpMap(Map<String , String> requestMap) {
		if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("password")) {
			return true;
		}
		return false;
	}
	
	private User getUserFromMap(Map<String, String> requestMap) {
		User user=new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("Inside login");
		try {
			
			Authentication auth=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))); 
			
			if(auth.isAuthenticated()) {
				if(customerUserDetailsService.getUserDatails().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>("{token:"+
				jwtUtils.generateToken(customerUserDetailsService.getUserDatails().getEmail(),
                            customerUserDetailsService.getUserDatails().getRole()) +"}",
				HttpStatus.OK);
				}else {
					return new ResponseEntity<String>("{ message : Wait for Admin approval. }",HttpStatus.BAD_REQUEST);
				}
			}
		}catch (Exception e) {
			log.error("{}",e);
		}
		return new ResponseEntity<String>("{ message : Bad Credentials. }",HttpStatus.BAD_REQUEST);
	
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
			}else {
				return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new  ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin())
			{
				Optional<User> optional=userDao.findById(Integer.parseInt(requestMap.get("id")));
				if(!optional.isEmpty()) {
					
					userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
				//	sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
					return  CafeUtils.getResponseEntity("User status Updated Successfully", HttpStatus.OK);

				}else {
					return  CafeUtils.getResponseEntity("User id doesn't not exist", HttpStatus.OK);

				}
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.INTERNAL_SERVER_ERROR);

			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			
			User userObj=userDao.findByEmail(jwtFilter.getCurrentUser());
			if(!userObj.equals(null)) {
				if(userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return CafeUtils.getResponseEntity("Password updated Successfully", HttpStatus.OK);
					
				}
				return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
	try {
		User user=userDao.findByEmail(requestMap.get("email"));
		if(!Objects.isNull(user)&& !Strings.isNullOrEmpty(user.getEmail())) {
			
		}
		
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	
	

//	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
//		allAdmin.remove(jwtFilter.getCurrentUser());
//		if(status!=null && status.equalsIgnoreCase("true")) {
//			 mailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved","User:-"+user+"\n is approved by \n ADMIN:-"+jwtFilter.getCurrentUser(), allAdmin);
//		}else {
//			 mailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled","User:-"+user+"\n is Disabled by \n ADMIN:-"+jwtFilter.getCurrentUser(), allAdmin);
//				
//		}
//	}
	
	
	
}
