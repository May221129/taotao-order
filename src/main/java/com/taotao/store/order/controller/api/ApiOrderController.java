package com.taotao.store.order.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.store.order.pojo.Order;
import com.taotao.store.order.pojo.TaotaoResult;
import com.taotao.store.order.service.OrderService;

/**
 * 对外提供接口服务：订单处理
 * @author Administrator
 *
 */
@RequestMapping("api/order")
@Controller
public class ApiOrderController {

	@Autowired
	private OrderService orderService;
	
	/**
	 * 创建订单
	 * 问题：订单编号不是自增，那又是如何生成的呢？
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ResponseEntity<Object> create(@RequestBody Order order){
		TaotaoResult taotaoResult = this.orderService.create(order);
		Object data = taotaoResult.getData();
		if(null != data){
			return ResponseEntity.status(HttpStatus.OK).body(data);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
	
}
