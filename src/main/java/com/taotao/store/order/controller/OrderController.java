package com.taotao.store.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.store.order.pojo.Order;
import com.taotao.store.order.pojo.PageResult;
import com.taotao.store.order.pojo.TaotaoResult;
import com.taotao.store.order.service.OrderService;

/**
 * 订单
 */
@RequestMapping("order")
@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	/**
	 * 创建订单
	 * 问题：订单编号不是自增，那又是如何生成的呢？
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<TaotaoResult> create(@RequestBody Order order){
		TaotaoResult result = this.orderService.create(order);
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 根据订单ID查询订单(查询结果包括：订单、商品快照、物流地址信息)
	 */
	@RequestMapping(value = "/query/{orderId}", method = RequestMethod.GET)
	public ResponseEntity<Order> queryOrderByOrderId(@PathVariable("orderId")String orderId){
		Order order = this.orderService.queryOrderByOrderId(orderId);
		if(null != order){
			return ResponseEntity.ok(order);
		}else{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	/**
	 * 根据用户昵称分页查询订单
	 */
	@RequestMapping("/query/{buyerNick}/{page}/{rows}")
	public ResponseEntity<PageResult<Order>> queryOrderPageByBuyerNick(
			@PathVariable("buyerNick")String buyerNick,
			@PathVariable("page")Integer page,
			@PathVariable("rows")Integer rows){
		PageResult<Order> pageResult = this.orderService.selectOrderByBuyerNick(buyerNick, page, rows);
		if(null != pageResult){
			return ResponseEntity.ok(pageResult);
		}else{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	/**
	 * 修改订单状态（更改付款状态、时间）
	 */
	@RequestMapping(value = "/changeOrderStatus", method = RequestMethod.POST)
	public ResponseEntity<Void> changeOrderStatus(@RequestBody Order order){
		Boolean bool = this.orderService.changeOrderStatus(order);
		if(bool){
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}else{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
