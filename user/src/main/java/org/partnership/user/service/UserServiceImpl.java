package org.partnership.user.service;

import java.util.HashSet;
import java.util.Set;
import org.partnership.user.model.Role;
import org.partnership.user.model.User;
import org.partnership.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RoleService roleService;

	@Autowired
	private SecurityService securityService;

	@Transactional
	public String save(User user, BindingResult bindingResult, String role, RedirectAttributes redirectAttributes) {
		if (!user.getPassword().equals(user.getPasswordConfirm()))
			bindingResult.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
		if (userRepository.findUserPresent(user.getEmail())){
			bindingResult.rejectValue("email", "Duplicate.email");
		}
		if (bindingResult.hasErrors()){
			String messages = "";
			String err = "";
			for (Object object : bindingResult.getAllErrors()) {
			    if(object instanceof FieldError) {
			        FieldError fieldError = (FieldError) object;
			        err =  fieldError.getDefaultMessage();
			    }
		        messages = messages+" "+ err;
			}
			redirectAttributes.addFlashAttribute("ERROR_MESSAGE", messages);
			return "redirect:/";
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		Set<Role> roles = new HashSet<Role>();
		roles.add(roleService.findByName(role));
		user.setRoles(roles);
		userRepository.save(user);
		securityService.autologin(user.getEmail(), user.getPasswordConfirm());
		redirectAttributes.addFlashAttribute("SUCCESS_MESSAGE", "Welcome !");
		return "redirect:/";
	}

	public User findOne(long id) {
		return userRepository.findOne(id);
	}

	public boolean findUserPresent(String email) {
		return userRepository.findUserPresent(email);
	}

}
