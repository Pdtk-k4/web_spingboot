
<div class="container py-5">
    <div class="lmm1">
        <div class="lmm6">
            <div class="lmm7">
                <h3 class="lmm2">Profile Customer</h3>
                <form method="POST" action="">
                    <table class="lmm3">
                        <tr class="lmm5">
                            <td class="lmm4">Name</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="text" name="name" value="">
                            </td>
                        </tr>
                        <tr class="lmm5">
                            <td class="lmm4">Email</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="email" name="email" value="">
                            </td>
                        </tr>
                        <tr class="lmm5">
                            <td class="lmm4">Phone</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="text" name="phone" value="">
                            </td>
                        </tr>
                        <tr class="lmm5">
                            <td class="lmm4">Address</td>
                            <td class="lmm4">:</td>
                            <td class="lmm4">
                                <input type="text" name="address" value="">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                <input type="submit" value="Cập nhật" style="margin-top: 10px;">
                            </td>
                        </tr>
                        <?php
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