package dk.gilakathula.webservice.web.orders;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/orders")
@Api(tags = "External", description = "Orders API, provides REST methods to place an order and  Retrieving all orders within a given time period ")
public class OrdersController {

    private OrderRepository orderRepository;
    private OrdersHelper ordersHelper;

    @Autowired
    public OrdersController(OrderRepository orderRepository, OrdersHelper ordersHelper) {
        this.orderRepository = orderRepository;
        this.ordersHelper = ordersHelper;
    }

    @ApiOperation(
            value = "List of Order Data by given 'n' number days"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Order Data by given 'n' number days", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/time-frame:days/{days}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> orders(@PathVariable(value = "days") int days) {
        final List<Order> orders = orderRepository.getAll(days);
        if (!orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(orders);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Unable to find any Orders");
        }
    }


    @ApiOperation(
            value = "Create new order"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Order Data", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not found"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/create:new", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> create(@RequestBody String payload) {
        final OrderStatus orderStatus = ordersHelper.placeOrder(payload);
        return ResponseEntity.status(orderStatus.getHttpStatus()).body(orderStatus.getMessage());
    }


}
