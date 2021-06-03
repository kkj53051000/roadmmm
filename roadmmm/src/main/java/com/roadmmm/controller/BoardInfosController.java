package com.roadmmm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.roadmmm.domain.boardinfos.BoardInfos;
import com.roadmmm.service.boardinfos.BoardInfosService;
import com.roadmmm.vo.BoardInfosForm;

@Controller
public class BoardInfosController {
	@Autowired
	private BoardInfosService boardInfosService;
	
	@GetMapping("/biwirte")
	public String BoardsInfoModifyPage(){
		return "boardInfosWrite";
	}
	
	@GetMapping("/biadmin")
	public String BoardsInfoAdmin(Model model){
		
		List<BoardInfos> boardInfos = boardInfosService.getBoardInfos();
		
		model.addAttribute("vo", boardInfos);
		
		
		return "boardInfosAdmin";
	}
	
	@PostMapping("/boardsinfoprocess")
	public String BoardInfoProcess(BoardInfosForm boardInfosForm){
		
		boardInfosService.saveBoardInfos(boardInfosForm);
		
		return "redirect:/biwirte";
	}
}
