package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;

@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	///Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;

	///Constructor
	public PurchaseController() {
		System.out.println(this.getClass());
	}
	
	@RequestMapping(value="addPurchase", method=RequestMethod.GET)
	public String addPurchase(@RequestParam("prodNo") int prodNo, Model model) throws Exception {
		
		System.out.println("/purchase/addPurchase : GET");
		
		Product product = productService.getProduct(prodNo);
		model.addAttribute("product", product);
		
		return "forward:/purchase/addPurchaseView.jsp";
	}
	
	@RequestMapping(value="addPurchase", method=RequestMethod.POST)
	public String addPurchase(@ModelAttribute("purchase") Purchase purchase, @RequestParam("prodNo") int prodNo, Model model, HttpSession session) throws Exception {

		System.out.println("/purchase/addPurchase : POST");
		
		User user = (User) session.getAttribute("user");
		Product product = productService.getProduct(prodNo);

		purchase.setBuyer(user);
		purchase.setPurchaseProd(product);
		
		System.out.println("DAOImpl purchase: "+purchase.toString());

		purchaseService.addPurchase(purchase);
		model.addAttribute("purchase", purchase);
		
		return "redirect:/purchase/addPurchase.jsp";
	}
	
	@RequestMapping(value="listPurchase")
	public String listPurchase(@ModelAttribute("search") Search search, Model model, HttpSession session, HttpServletRequest request) throws Exception {
		
		System.out.println("/purchase/listPurchase : GET/POST");
		
		String buyerId = ((User)session.getAttribute("user")).getUserId();
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String, Object> map = purchaseService.getPurchaseList(search, buyerId);
				
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/purchase/listPurchase.jsp";
	}
	
	@RequestMapping(value="listSale")
	public String listSale(@ModelAttribute("search") Search search, Model model, HttpServletRequest request) throws Exception {

		System.out.println("/purchase/listSale : GET/POST");

		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String, Object> map = purchaseService.getSaleList(search);
				
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/purchase/listSale.jsp";
	}
	
	@RequestMapping(value="getPurchase", method=RequestMethod.GET)
	public String getPurchase(@RequestParam("tranNo") int tranNo, Model model) throws Exception {
		
		System.out.println("/purchase/getPurchase?tranNo= : GET");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping(value="getPurchaseByProdNo", method=RequestMethod.GET)
	public String getPurchase2(@RequestParam("prodNo") int prodNo, Model model) throws Exception {
		
		System.out.println("/purchase/getPurchase?prodNo= : GET");
		
		Purchase purchase = purchaseService.getPurchase2(prodNo);
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping(value="updatePurchase", method=RequestMethod.GET)
	public String updatePurchase(@RequestParam("tranNo") int tranNo, Model model) throws Exception {
		
		System.out.println("/purchase/updatePurchase : GET");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/updatePurchaseView.jsp";
	}
	
	@RequestMapping(value="updatePurchase", method=RequestMethod.POST)
	public String updatePurchase(@ModelAttribute("purchase") Purchase purchase, Model model) throws Exception {
		
		System.out.println("/purchase/updatePurchase : POST");
		System.out.println(purchase.toString());
		
		purchaseService.updatePurcahse(purchase);
		Purchase returnPurchase = purchaseService.getPurchase(purchase.getTranNo());
		model.addAttribute("purchase", returnPurchase);
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping(value="updateTranCodeByProd", method=RequestMethod.GET)
	public String updateTranCodeByProd(@RequestParam("tranCode") String tranCode, @RequestParam("tranNo") int tranNo) throws Exception {
		
		System.out.println("purchase/updateTranCodeByProd : GET");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		if( purchase.getTranCode() != null && !(purchase.getTranCode().equals("003")) ) {
			if(purchase.getTranCode().equals("001")) {
				purchase.setTranCode("002");
			} else if(purchase.getTranCode().equals("002")) {
				purchase.setTranCode("003");
			}	
			purchaseService.updateTranCode(purchase);
		}
		return "redirect:/purchase/listSale";
	}

	@RequestMapping(value="updateTranCode", method=RequestMethod.GET)
	public String updateTranCode(@RequestParam("tranCode") String tranCode, @RequestParam("tranNo") int tranNo) throws Exception {
		
		System.out.println("purchase/updateTranCode : GET");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		if( purchase.getTranCode() != null && purchase.getTranCode().equals("002") ) {
				purchase.setTranCode("003");
				purchaseService.updateTranCode(purchase);
			}	
	
		return "redirect:/purchase/listPurchase";
	}
}
