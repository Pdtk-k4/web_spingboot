<?php
include 'inc/header.php';
include_once './classes/customer.php';
include_once './lib/session.php';
Session::init();

$cs = new customer();
$login_check = Session::get('customer_login');
if ($login_check == false) {
    header('Location: login.php');
    exit();
}

$id = Session::get('customer_id');

// Xử lý update khi submit form
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $update_result = $cs->update_customer($_POST, $id);
}
?>
<div class="container py-5">
    <div class="lmm1">
        <div class="lmm6">
            <div class="lmm7">
                <h3 class="lmm2">Profile Customer</h3>
                <?php if (isset($update_result)) echo $update_result; ?>
                <form method="POST" action="">
                    <table class="lmm3">
                        <?php
                        $get_customers = $cs->show_customers($id);
                        if ($get_customers && $result = $get_customers->fetch_assoc()) {
                        ?>
                        <tr class="lmm5">
                            <td class="lmm4">Name</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="text" name="name" value="<?php echo htmlspecialchars($result['name']); ?>">
                            </td>
                        </tr>
                        <tr class="lmm5">
                            <td class="lmm4">Email</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="email" name="email" value="<?php echo htmlspecialchars($result['email']); ?>">
                            </td>
                        </tr>
                        <tr class="lmm5">
                            <td class="lmm4">Phone</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="text" name="phone" value="<?php echo htmlspecialchars($result['phone'] ?? ''); ?>">
                            </td>
                        </tr>
                        <tr class="lmm5">
                            <td class="lmm4">Address</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="text" name="address" value="<?php echo htmlspecialchars($result['address'] ?? ''); ?>">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                <input type="submit" value="Cập nhật" style="margin-top: 10px;">
                            </td>
                        </tr>
                        <?php
                        } else {
                            echo "<tr><td colspan='3'>Không tìm thấy thông tin khách hàng!</td></tr>";
                        }
                        ?>
                    </table>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    if (window.history.replaceState) {
    window.history.replaceState(null, null, window.location.href);
}
</script>

<?php
include 'inc/footer.php';
?>