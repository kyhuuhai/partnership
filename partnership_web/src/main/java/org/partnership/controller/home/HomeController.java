package org.partnership.controller.home;


import javax.validation.Valid;

import org.partnership.container.PartnershipFlash;
import org.partnership.user.model.Contact;
import org.partnership.user.model.User;
import org.partnership.user.service.ContactService;
import org.partnership.user.service.UserCustom;
import org.partnership.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private ContactService contactService;
	
	@RequestMapping(value = "/")
	private String home() {
		return "home";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registration(@Valid User user, BindingResult bindingResult, Model model,
			@RequestParam("role") String role, RedirectAttributes redirectAttributes) {
		return userService.save(user, bindingResult, role, redirectAttributes);
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout) {
		if (error != null)
			model.addAttribute("MESSAGE", PartnershipFlash.getFlashError("Your email and password is invalid."));
		if (logout != null)
			model.addAttribute("MESSAGE", PartnershipFlash.getFlashSuccess("You have been logged out successfully."));
		return "home";
	}
	
	@RequestMapping(value= "/showContact")
	public String showContact(Model model){
		UserCustom user = (UserCustom)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user == null){
			return "redirect:/";
		}else{
			model.addAttribute("contacts", contactService.findAllInbox(user.getId()));
			model.addAttribute("notdeleted", contactService.countByNotDeleted(user.getId()));
			return "showcontact";
		}
	}
	
	@RequestMapping(value= "/inbox")
	public String readContact(Model model){
		UserCustom user = (UserCustom)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user == null){
			return "redirect:/";
		}
		return "showcontact";
	}
	
	@RequestMapping(value = "/changeStatus/{id}")
	public @ResponseBody long changeStatus(@PathVariable int id, Model model){
		Contact contact = contactService.findOne(id);
		contact.setStatus(1);
		contactService.saveContact(contact);
		return contactService.countCountactByNotSeen(contact.getUserReceive().getId(), 0);
	}

}
