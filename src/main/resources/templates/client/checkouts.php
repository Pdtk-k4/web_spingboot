<?php
ob_start();
error_reporting(E_ALL);
ini_set('display_errors', 1);
    include "inc/header.php";
    if(isset($_GET['tstId']) && $_GET['tstId']!=NULL){
        $tstId = $_GET['tstId'];
    }
    if($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['submit'])){
        $cusId = Session::get('customer_id');
        $insertInbox = $cart->insertInbox($_POST, $tstId, $cusId);
    
     
            $cart->del_cart($tstId); // Xóa giỏ hàng sau khi đặt hàng thành công
            header("Location: cart.php");
            exit;
        
    }
    
?>
<div class="container py-5">
    <div class="row">
        <div class="col-12 col-lg-7">
            <?php
                $getTransactionsByCusId = $cart->getTransactionsByCusId(Session::get('customer_id'));
                if($getTransactionsByCusId){
                    while($resultTransactions = $getTransactionsByCusId->fetch_assoc()){
                        $tstId = $resultTransactions['tstId'];
                        $getListOrder = $cart->getListOrder($tstId);
                        $count = $cart->count($tstId);
            ?>
            <div class="heading-page">
                <div class="header-page">
					<h1>Thông tin đơn hàng</h1>
                </div>
            </div>
            <div class="row wrapbox-content-cart">
                <div class="row wrapbox-content-cart">
                    <div class="cart-container">
                        <div class="cart-col-left">
                            <div class="main-content-cart">
                                <form action="" id="cartformpage">
                                    <div class="row">
                                        <div class="col-md-12 col-sm-12 col-xs-12">
                                            <table class="table-cart">
                                                <thead>
													<tr>
														<th class="image">&nbsp;</th>
														<th class="px-3">Tên sản phẩm</th>
														<th class="item">Số lượng</th>
														<th class="item">Thành tiền</th>
														<th class="remove">&nbsp;</th>
													</tr>
												</thead>
                                                <tbody>
                                                    <?php
                                                        if($getListOrder){
                                                            $i = 0;
                                                            while($resultOrder = $getListOrder->fetch_assoc()){
                                                                $i++;
                                                    ?>
                                                    <tr class="line-item-container">
                                                        <td class="image">
															<div class="product_image" style="width: 4.6em;">
																<a href="detail.php?proId=<?php echo $resultOrder['proId']; ?>">
																	<img style="max-width: 100%;" src="admin/uploads/<?php echo $resultOrder['proImage']; ?>" alt="<?php echo $resultOrder['proName']; ?>">
																</a>
															</div>
														</td>
                                                        <td class="item" style="max-width: 195px;">
															<h3><a href="detail.php?proId=<?php echo $resultOrder['proId']; ?>"><?php echo $resultOrder['proName']; ?></a></h3>
														</td>
                                                        <td class="qty">
                                                            <div class="qty quantity-partent qty-click clearfix" data-proid="<?php echo $resultOrder['proId']; ?>" data-tstid="<?php echo $tstId; ?>">
                                                                <button type="button" class="qtyminus qty-btn">-</button>
                                                                <input type="text" value="<?php echo $resultOrder['odQuantity']?>" class="tc line-item-qty item-quantity bg-white">
                                                                <button type="button" class="qtyplus qty-btn">+</button>															
                                                            </div>
														</td>
                                                        <td class="item">
															<p class="">
																<span class="line-item-total price"><?php echo number_format($resultOrder['tongTien'], 0, '.', '.'); ?>₫</span>
															</p>
														</td>
                                                        

                                                    </tr>
                                                    <?php
                                                        }
                                                    }
                                                    ?>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-12 col-md-5 col-lg-7">
                                            <div class="sidebox-group">
                                            </div>
                                        </div>
                                        <div class="col-12 col-md-7 col-lg-5">
                                            <div class="sidebox-order">
                                                <div class="sidebox-order-inner">
                                                    <div class="sidebox-order_total">
														<p>Tổng tiền: <span class="total-price"><?php echo number_format($resultTransactions['tstTotalMoney'], 0, '.', '.'); ?>₫</span></p>
													</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <?php
                    }
                }else{
            ?>
            <div class="heading-page">
                <div class="header-page">
					<h1>Thông tin đơn hàng</h1>
					<p class="count-cart">Có <span>0 sản phẩm </span>trong giỏ hàng</p>
                </div>
            </div>
            <a href="index.php"><span>Tiếp tục mua hàng</span></a>
            <?php
                }
            ?>
        </div>
        <div class="col-12 col-lg-5">
            <h3>Thông tin giao hàng</h3>
            <?php
                $getCusstomer = $cs->getCustomer(Session::get('customer_id'));
                if($getCusstomer){
                    $resultCustomer = $getCusstomer->fetch_assoc();
            ?>
            <div class="user-info">
                <div class="user-avatar">👤</div>
                <div class="user-details">
                    <strong><?php echo $resultCustomer['name'] ?></strong> (<?php echo $resultCustomer['email'] ?>) <br>
                    <a href="#" class="logout">Đăng xuất</a>
                </div>
            </div>
            <form action="" method="POST">
                <div class="input-group">
                    <select name="address">
                        <option>Thêm địa chỉ mới...</option>
                        <option selected>70000, Vietnam</option>
                    </select>
                </div>
                <div class="input-group">
                    <input type="text" name="name" placeholder="Họ và tên" required value='<?php echo $resultCustomer['name'] ?>'>
                </div>
                <div class="input-group">
                    <input type="text" name="phone" placeholder="Số điện thoại" required value='<?php echo $resultCustomer['phone'] ?>'>
                </div>
                <div class="input-group">
                    <input type="text" name="address" placeholder="Địa chỉ" required value='<?php echo $resultCustomer['address'] ?>'>
                </div>
                <div class="payment-method">
                    <h3>Phương thức thanh toán</h3>
                    <label class="payment-option">
                        <input type="radio" name="payment" value="cod" checked>
                        <img  src="https://hstatic.net/0/0/global/design/seller/image/payment/cod.svg?v=6" alt="COD"><span  style="font-size: 14px;">Thanh toán khi giao hàng (COD)</span>
                    </label>
                </div>
                <button type="submit" name="submit" class="btn-submit">Đặt hàng</button>
            </form>
            <?php
                }
            ?>
        </div>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function () {
    // Lắng nghe sự kiện click cho tất cả nút "+" và "-"
    document.querySelectorAll(".quantity-partent").forEach(function (parent) {
        let minusBtn = parent.querySelector(".qtyminus");
        let plusBtn = parent.querySelector(".qtyplus");
        let inputField = parent.querySelector(".item-quantity");

        let proId = parent.getAttribute("data-proid");
        let tstId = parent.getAttribute("data-tstid");

        // Hàm cập nhật số lượng
        function updateQuantity(change) {
            let currentQuantity = parseInt(inputField.value);
            let newQuantity = currentQuantity + change;

            if (newQuantity < 1) return; // Không cho phép số lượng nhỏ hơn 1

            inputField.value = newQuantity;

            // Gửi AJAX cập nhật số lượng trong giỏ hàng
            fetch("update_cart.php", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `proId=${proId}&tstId=${tstId}&quantity=${newQuantity}`
            })
            .then(response => response.text())
            .then(data => {
                console.log(data); // Kiểm tra phản hồi từ PHP
                location.reload(); // Tải lại trang để cập nhật tổng tiền
            });
        }

        // Gán sự kiện cho nút trừ (-)
        minusBtn.addEventListener("click", function () {
            updateQuantity(-1);
        });

        // Gán sự kiện cho nút cộng (+)
        plusBtn.addEventListener("click", function () {
            updateQuantity(1);
        });
    });
});
</script>
<script>
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".remove a").forEach(function (removeBtn) {
        removeBtn.addEventListener("click", function (event) {
            event.preventDefault(); // Ngăn chặn hành vi mặc định của thẻ <a>

            let parentRow = this.closest(".line-item-container"); // Dòng sản phẩm cần xóa
            let proId = parentRow.querySelector(".quantity-partent").getAttribute("data-proid");
            let tstId = parentRow.querySelector(".quantity-partent").getAttribute("data-tstid");

            if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
                fetch("remove_cart.php", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `proId=${proId}&tstId=${tstId}`
                })
                .then(response => response.text())
                .then(data => {
                    console.log("Server Response:", data); // Kiểm tra phản hồi từ server

                    if (data.trim() === "success") {
                        location.reload(); // Tải lại trang sau khi xóa thành công
                    } else {
                        alert("Xóa sản phẩm thất bại, vui lòng thử lại!");
                    }
                })
                .catch(error => {
                    console.error("Lỗi khi gửi yêu cầu xóa:", error);
                });
            }
        });
    });
});


</script>
<?php
    include "inc/footer.php";
?>
<?php
ob_end_flush(); // Kết thúc output buffering và gửi dữ liệu ra trình duyệt
?>
