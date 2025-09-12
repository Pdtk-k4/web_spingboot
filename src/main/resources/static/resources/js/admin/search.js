

    $(document).ready(function() {
    $('.input-search').on('input', function() {
        let query = $(this).val().trim();
        let resultsContainer = $('#ajaxSearchResults-3 .resultsContent');

        if (query.length < 2) {
            resultsContainer.empty().hide();
            return;
        }

        $.ajax({
            url: '/admin/search',
            type: 'GET',
            data: { query: query },
            success: function(response) {
                console.log('Phản hồi JSON:', response); // Log để debug
                resultsContainer.empty();
                if (response.results && response.results.length > 0) {
                    let html = '<ul class="search-results">';
                    response.results.forEach(function(product, index) {
                        console.log('Sản phẩm:', product); // Log từng sản phẩm
                        html += `
                                <li class="search-result-item" data-product-id="${product.id}">
                                    <img src="/resources/uploads/${product.proimage || 'default.jpg'}" alt="${product.proname}">
                                    <div>
                                        <span>${product.proname}</span>
                                        <small>Tác giả: ${product.author && product.author.auname ? product.author.auname : 'Không có tác giả'}</small>
                                    </div>
                                </li>`;
                    });
                    html += '</ul>';
                    resultsContainer.html(html).show();
                } else {
                    resultsContainer.html('<p>Không tìm thấy sản phẩm</p>').show();
                }
            },
            error: function(xhr, status, error) {
                console.log('Lỗi AJAX:', xhr.status, xhr.responseText);
                resultsContainer.html(`<p>Lỗi khi tìm kiếm sản phẩm: ${xhr.status} - ${error}</p>`).show();
            }
        });
    });

    $(document).on('click', '.search-result-item', function() {
    let productName = $(this).find('span').text();
    let productImage = $(this).find('img').attr('src');
    $('.input-search').val(productName);
    $('#previewCover').attr('src', productImage);
    $('#ajaxSearchResults-3 .resultsContent').hide();
});

    $(document).click(function(event) {
    if (!$(event.target).closest('.smart-search-wrapper, .input-search').length) {
    $('#ajaxSearchResults-3 .resultsContent').hide();
}
});
});