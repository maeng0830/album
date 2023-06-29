// 첫 번째 페이지 가져오기
$(document).ready(function () {

  var param = '?page=0&size=3';

  $.ajax({
    type: 'GET',
    url: '/feeds' + param,
    success: function (feeds) {
      if (!feeds.length) {
        document.querySelector('#feedList')
            .innerHTML = '<div class="col-7 mb4">등록된 피드가 없습니다.</div>>'
      } else {
        var feedHtml = '';

        feeds.forEach(feed => {
          feedHtml += '<div class="col-7 mb-4">\n'
              + '          <div class="card shadow-sm">\n'
              + '            <img th:if="${feed.feedImages.size() ne 0}" th:src="${feed.feedImages[0].imagePath}" class="bd-placeholder-img card-img-top" width="100%" height="225" role="img" aria-label="Card Image" preserveAspectRatio="xMidYMid slice" focusable="false">\n'
              + '            <img th:if="${feed.feedImages.size() eq 0}" src="#" class="bd-placeholder-img card-img-top" width="100%" height="225" role="img" aria-label="Card Image" preserveAspectRatio="xMidYMid slice" focusable="false">\n'
              + '            <div class="card-body">\n'
              + '              <h5 th:text="${feed.title}" class="card-title">카드 제목</h5>\n'
              + '              <p  th:text="${feed.content}" class="card-text">카드 내용</p>\n'
              + '              <div class="d-flex justify-content-between align-items-center">\n'
              + '                <div class="btn-group">\n'
              + '                  <a th:href="/member-view/${feed.createdById}" class="rounded-circle">\n'
              + '                    <img th:if="${feed.createdByImage != null}" th:src="${feed.createdByImage}" alt="작성자 이미지" width="30" height="30">\n'
              + '                    <img th:if="${feed.createdByImage == null}" src="#" alt="작성자 이미지" width="30" height="30">\n'
              + '                  </a>\n'
              + '                </div>\n'
              + '                <div>\n'
              + '                  <small th:text="댓글(${feed.commentCount})" class="text-muted">댓글</small>\n'
              + '                  <br>\n'
              + '                  <small th:text="${feed.createdAt}" class="text-muted">작성시간</small>\n'
              + '                </div>\n'
              + '              </div>\n'
              + '            </div>\n'
              + '          </div>\n'
              + '        </div>';
        })

        document.querySelector('#feedList').innerHTML = feedHtml;
      }
    },
    error: function () {
      alert("피드 목록을 가져오는데 실패했습니다.");
    }
  })
})

// 더보기 버튼으로 다음 페이지 가져오기