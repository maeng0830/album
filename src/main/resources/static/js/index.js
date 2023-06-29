let currentPage = 0;

$(document).ready(getFeeds);

document.querySelector('#loadMoreBtn').addEventListener('click', function () {
  getFeeds();
})

function getFeeds() {

  let param = '?page=' + currentPage + '&size=3';
  currentPage +=1;

  $.ajax({
    type: 'GET',
    url: '/feeds' + param,
    success: function (feeds) {
      if (!feeds.length) {
        alert('더 이상 등록된 피드가 없습니다.');
      } else {
        var feedHtml = '';

        feeds.forEach(feed => {
          var feedImage = feed.feedImages.length !== 0 ? feed.feedImages[0].imagePath : '#';
          var createdByImage = feed.createdByImage != null ? feed.createdByImage : '#';

          feedHtml += `
            <div class="col-7 mb-4">
              <div class="card shadow-sm">
                <img src="${feedImage}" class="bd-placeholder-img card-img-top" width="100%" height="225" role="img" aria-label="Card Image" preserveAspectRatio="xMidYMid slice" focusable="false">
                <div class="card-body">
                  <h5 class="card-title">${feed.title}</h5>
                  <p class="card-text">${feed.content}</p>
                  <div class="d-flex justify-content-between align-items-center">
                    <div class="btn-group">
                      <a href="/member-view/${feed.createdById}" class="rounded-circle">
                        <img src="${createdByImage}" alt="작성자 이미지" width="30" height="30">
                      </a>
                    </div>
                    <div>
                      <small class="text-muted">댓글(${feed.commentCount})</small>
                      <br>
                      <small class="text-muted">${feed.createdAt}</small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          `;
        })

        if (currentPage === 1) {
         document.querySelector('#feedList').innerHTML = feedHtml;
        } else {
          document.querySelector('#feedList').insertAdjacentHTML('beforeend', feedHtml);
        }
      }
    },
    error: function () {
      alert("피드 목록을 가져오는데 실패했습니다.");
    }
  })
}