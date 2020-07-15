package my.spring.opalproject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import model.dao.CartDAO;
import model.vo.CartVO;
import model.vo.GoodsVO;
import service.CartService;

@Controller
//@RequestMapping("/opalproject/cart/*")
public class CartController {

	@Autowired
	CartService cartService;

	@Autowired
	CartDAO dao;

	
	// 1. 장바구니 추가

	@RequestMapping(value="/cart/insert", method=RequestMethod.POST)
	public String insert(@ModelAttribute CartVO vo, HttpSession session){
		// product_name
		// money
		// product_cd = 34
		
		System.out.println("insert의 VO"+vo);
		//System.out.println(cust_cd);
		
		//장바구니에 기존 상품이 있는지 검사
		int count = cartService.countCart(vo.getProduct_cd(), vo.getCust_cd());
		System.out.println(count);
		//System.out.println("cart controller : "+count);
		if(count==0){ 
			// 없으면insert 
			cartService.insert(vo); 
			//System.out.println("insert");
		} else { 
			//있으면 update 
			cartService.updateCart(vo);
			System.out.println("---after updateCart---");
			System.out.println(vo);
			System.out.println("--------------");
			//System.out.println("update");
			session.setAttribute("vo", vo);

		}
		return"redirect:list";
	}

	// 2. 장바구니 목록

	@RequestMapping(value="/cart/list", method=RequestMethod.GET)
	  public ModelAndView list(HttpSession session, ModelAndView mav, CartVO vo) throws IndexOutOfBoundsException{
		Map<String,Object> map = new HashMap<String, Object>();
		
		
		CartVO vo1 = (CartVO)session.getAttribute("vo");

	    List<CartVO> list = cartService.listCart(vo1.getCust_cd());//장바구니 정보
	    
	    
	    System.out.println("컨트롤러의 "+list);
	    int sumMoney = cartService.sumMoney(vo1.getCust_cd());// 장바구니 전체 금액 호출
	    //System.out.println("sumMoney : "+ sumMoney);
	  // 장바구니 전체 긍액에 따라 배송비 구분 
	  //배송료(10만원이상 => 무료, 미만 => 2500원) 
	      int fee = sumMoney >= 100000 ? 0 : 2500;
		/*
		 * map.put("list", list); // 장바구니 정보를 map에 저장 map.put("count", list.size());
		 * //장바구니 상품의 유무
		 */		  
	      map.put("sumMoney", sumMoney); // 장바구니 전체 금액 
		  map.put("fee", fee); // 배송금액
		  map.put("cust_cd", vo1.getCust_cd());
		  map.put("allSum", sumMoney+fee); // 주문 상품 전체 금액
		  if(!list.isEmpty()) {
		  map.put("cart_cd", list.get(0).getCart_cd());
		  map.put("list", list); // 장바구니 정보를 map에 저장 
		  map.put("count", list.size()); //장바구니 상품의 유무 
		  }

		  mav.addObject("map", map); // map 변수 저장 
		  System.out.println("--map--" + map.get("list"));
		  mav.setViewName("cartlist"); //view(jsp)의 이름 저장 
		  return mav; 	
	  }

	// 3. 장바구니 삭제

	@RequestMapping(value="/cart/delete", method=RequestMethod.GET)
	@ResponseBody
	public void delete(@RequestParam int cart_cd, HttpServletResponse response) throws ServletException, IOException{
	 cartService.delete(cart_cd, response);

	} 	
	/*
	 * public String delete(@RequestParam int cart_cd){
	 * System.out.println("delete: cart_cd : "+ cart_cd);
	 * cartService.delete(cart_cd); return "redirect:list"; }
	 */

	// 4. 장바구니 수정
	/*
	 * @RequestMapping(value="/cart/update", method=RequestMethod.POST)
	 * 
	 * @ResponseBody public void update(@RequestParam int cart_amount, @RequestParam
	 * int product_cd, @RequestParam (defaultValue="1") int cust_cd,
	 * HttpServletResponse response) throws ServletException, IOException { //
	 * session의 id //cust_cd = 1; CartVO vo = new CartVO();
	 * System.out.println(product_cd);
	 * 
	 * //(int)session.getAttribute("cust_cd"); // 레코드의 갯수 만큰 반복문 실행 for(int
	 * i=0;i<product_cd; i++){ vo.setCust_cd(cust_cd);
	 * vo.setCart_amount(cart_amount); vo.setProduct_cd(product_cd);
	 * System.out.println(vo); cartService.modifyCart(vo, response); }
	 * System.out.println("일 다함"); //return "redirect:list"; }
	 */	 

}
