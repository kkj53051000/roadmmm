package com.roadmmm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.roadmmm.domain.PopularLive;
import com.roadmmm.domain.User;
import com.roadmmm.domain.boardinfos.BoardListEnum;
import com.roadmmm.domain.stockstudy.StockStudy;
import com.roadmmm.domain.stockstudy.StockStudyComment;
import com.roadmmm.domain.stockstudy.StockStudyRecommend;
import com.roadmmm.domain.stockstudy.StockStudyReply;
import com.roadmmm.domain.stockstudy.StockStudyTag;
import com.roadmmm.service.PopularService;
import com.roadmmm.service.UserService;
import com.roadmmm.service.boardinfos.BoardInfosService;
import com.roadmmm.service.stockstudy.StockStudyCommentService;
import com.roadmmm.service.stockstudy.StockStudyRecommendService;
import com.roadmmm.service.stockstudy.StockStudyReplyService;
import com.roadmmm.service.stockstudy.StockStudyService;
import com.roadmmm.vo.CommentEnum;
import com.roadmmm.vo.StockStudyCommentVo;
import com.roadmmm.vo.StockStudyContentVo;
import com.roadmmm.vo.StockStudyForm;
import com.roadmmm.vo.StockStudyListVo;
import com.roadmmm.vo.UserSessionForm;

@Controller
public class StockStudyController {
	
	@Autowired
	private StockStudyService stockStudyService;
	
	@Autowired
	private StockStudyRecommendService stockStudyRecommendService;
	
	@Autowired
	private StockStudyCommentService stockStudyCommentService;
	
	@Autowired
	private StockStudyReplyService stockStudyReplyService;
	
	@Autowired
	private BoardInfosService boardInfosService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PopularService popularService;
	
	@GetMapping("/sswrite")
	public String StockStudyWrite() {
		return "stockStudyWrite";
	}
	
	//????????? ??????.
	@GetMapping("/sslist")
	public String StockStudyList(HttpServletRequest request, Model model) {
		
		String sector = request.getParameter("sector");
		String page = request.getParameter("page");
		
		//ALL??? ????????? ENUM?????? ??????
		if(sector.equals("ALL")) {
			StockStudyListVo vo = stockStudyService.getStockStudyList(page, sector);
			
			model.addAttribute("vo", vo);
			
			return "stockStudyList";
		}else {
			StockStudyListVo vo = stockStudyService.getStockStudyListTag(page, sector);
			
			model.addAttribute("vo", vo);
			
			return "stockStudyList";
		}
	}
	
	
	//????????? ????????? ??????.
	@GetMapping("/ssbestlist")
	public String StockStudyBestList(HttpServletRequest request, HttpSession session, Model model) {
		
		String page = request.getParameter("page");
		
		int bestStandard = boardInfosService.getBoardInfosBestStandard("StockStudy");
		
		int start = 0;
		
		StockStudyListVo vo = stockStudyService.getStockStudyBestList(page, start, bestStandard);
		
		model.addAttribute("vo", vo);
		
		return "stockStudyBestList";
	}
	
	
	//??? ??????.
	@PostMapping("/stockstudyprocess")
	public String StockStudyProcess(HttpServletRequest request, HttpSession session, StockStudyForm stockStudyForm){
		
		UserSessionForm userSession = (UserSessionForm)session.getAttribute("user");
		
		if(userSession == null) {
			return "redirect:/";
		}
		for(int i = 0; i <= 101; i++) {
			User user = userService.getUser(userSession.getUser_id());
			
			Date now = new Date();
			
			System.out.println("now : " + now);
			
			StockStudy stockStudy = new StockStudy(stockStudyForm.getTitle(), stockStudyForm.getContent(), now, StockStudyTag.valueOf(stockStudyForm.getTag()), 0, 0, false, user);
			
			stockStudyService.saveStockStudy(stockStudy);
		}
		
		return "redirect:/sslist?sector=ALL";
	}
	
	@GetMapping("/sscontent")
	public String StockStudyContent(HttpServletRequest request, Model model) {
		
		long ssId = Long.parseLong(request.getParameter("id"));
		//id ?????????
		StockStudy stockStudy = stockStudyService.getStockStudy(ssId);
		
		//?????? ????????? ??????
		int upCount = stockStudyRecommendService.getStockStudyRecommendUpCount(ssId);
		int downCount = stockStudyRecommendService.getStockStudyRecommendDwonCount(ssId);
		
		//??????, ??????
		List<StockStudyCommentVo> stockStudyCommentVoList = new ArrayList<StockStudyCommentVo>();
		
		List<StockStudyComment> stockStudyComment = stockStudyCommentService.getStockStudyComments(ssId);
		
		for(int i =0; i < stockStudyComment.size(); i++) {
			StockStudyCommentVo stockStudyCommentVo = new StockStudyCommentVo(stockStudyComment.get(i).getId(), stockStudyComment.get(i).getContent(), stockStudyComment.get(i).getUser(), CommentEnum.COMMENT);
			stockStudyCommentVoList.add(stockStudyCommentVo);
			
			List<StockStudyReply> stockStudyReply = stockStudyReplyService.getStockStudyReplys(stockStudyComment.get(i).getId());
			
			if(stockStudyReply != null) {
				for(int j = 0; j < stockStudyReply.size(); j++) {
					stockStudyCommentVo = new StockStudyCommentVo(stockStudyReply.get(j).getId(), stockStudyReply.get(j).getContent(), stockStudyReply.get(j).getUser(), CommentEnum.REPLY);
					stockStudyCommentVoList.add(stockStudyCommentVo);
				}
			}
			
		}
		
		StockStudyContentVo vo = new StockStudyContentVo(stockStudy, stockStudyComment, upCount, downCount);	
		
		model.addAttribute("vo", vo);
		model.addAttribute("voc", stockStudyCommentVoList);
		
		return "stockStudyContent";
	}
	
	@GetMapping("/sscontentdeleteprocess")
	public String StockStudyContentDelete(HttpServletRequest request, HttpSession session) {
		UserSessionForm userSessionForm = (UserSessionForm)session.getAttribute("user");
		
		long ssId = Long.parseLong(request.getParameter("ssid"));
		
		StockStudy stockStudy = stockStudyService.getStockStudy(ssId);
		
		if(userSessionForm.getUser_id() != stockStudy.getUser().getId()) {
			return "redirect:/sslist";
		}
		
		stockStudyService.removeStockStudy(ssId);
		
		return "redirect:/sslist";
	}
	
	//?????? ??????.
	@GetMapping("/ssrecommendprocess")
	public String StockStudyRecommendProcess(HttpServletRequest request, HttpSession session) {
		UserSessionForm userSessionForm = (UserSessionForm)session.getAttribute("user");
		
		User user = userService.getUser(userSessionForm.getUser_id());
		
		boolean updown = Boolean.parseBoolean(request.getParameter("updown"));
		long ssId = Long.parseLong(request.getParameter("id"));
		
		//?????? ?????? ???????????? ??????
		boolean check = stockStudyRecommendService.checkStockStudyRecommend(ssId, user.getId());
		
		if(check == true) {
			return "redirect:/";
		}
		
		if(updown == true) {
			stockStudyService.addStockStudyUpCount(ssId);
		}else {
			stockStudyService.addStockStudyDownCount(ssId);
		}
		
		StockStudy stockStudy = stockStudyService.getStockStudy(ssId);
		
		//???????????? True
		int bestStandard = boardInfosService.getBoardInfosBestStandard("StockStudy");
		
		if(stockStudy.getUpCount() >= bestStandard) {
			stockStudyService.setStockStudyBestCheck(ssId);
		}
		
		//????????? ??????
		int popularStandard = boardInfosService.getBoardInfosPopularStandard("StockStudy");
		
		if(stockStudy.getUpCount() >= popularStandard) {
			PopularLive popularLive = new PopularLive("StockStudy", ssId, user);
			popularService.savePopularLive(popularLive);
		}
		
		StockStudyRecommend stockStudyRecommend = new StockStudyRecommend(updown, user, stockStudy);
		
		stockStudyRecommendService.saveStockStudyRecommend(stockStudyRecommend);
		
		return "redirect:/sscontent?id=" + ssId;
		
	}
	
	//?????? ??????.
	@PostMapping("/sscommentprocess")
	public String StockStudyCommentProcess(HttpServletRequest request, HttpSession session) {
		UserSessionForm userSessionForm = (UserSessionForm)session.getAttribute("user");
		
		User user = userService.getUser(userSessionForm.getUser_id());
		
		int ssId = Integer.parseInt(request.getParameter("ssid"));
		String content = request.getParameter("content");
		
		StockStudy stockStudy = stockStudyService.getStockStudy(ssId);
		
		StockStudyComment stockStudyComment = new StockStudyComment(content, user, stockStudy);
		
		stockStudyCommentService.saveStockStudyComment(stockStudyComment);
		
		return "redirect:/sscontent?id=" + ssId;
	}
	
	@PostMapping("/sscommentdeleteprocess")
	public String StockStudyCommentDeleteProcess(HttpServletRequest request, HttpSession session) {
		UserSessionForm userSessionForm = (UserSessionForm)session.getAttribute("user");
		
		Long userId = Long.parseLong(request.getParameter("userid"));
		Long sscmId = Long.parseLong(request.getParameter("sscmid"));
		
		//?????? User??? ????????????.
		if(userSessionForm.getUser_id() != userId) {
			
			return "redirect:/";
		}
		
		stockStudyCommentService.removeSotckStudyComment(sscmId);
		
		return "redirect:/";
	}
	
	//?????? ??????.
	@PostMapping("ssreplyprocess")
	public String StockStudyReplyProcess(HttpServletRequest request, HttpSession session) {
		UserSessionForm userSessionForm = (UserSessionForm)session.getAttribute("user");
		
		User user = userService.getUser(userSessionForm.getUser_id());
		
		String content = request.getParameter("content");
		
		long ssId = Long.parseLong(request.getParameter("ssid"));
		long sscId = Long.parseLong(request.getParameter("sscid"));
		
		StockStudyComment stockStudyComment = stockStudyCommentService.getStockStudyComment(sscId);
		
		StockStudyReply stockStudyReply = new StockStudyReply(content, stockStudyComment, user);
		
		stockStudyReplyService.saveStockStudyReply(stockStudyReply);
		
		return "redirect:/sscontent?id=" + ssId;
		
	}
	
	
}
