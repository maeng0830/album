let currentPage = 0;
let pageSize = 3;

function getMyFeeds(memberId) {

  let param = `?page=${currentPage}&size=${pageSize}`;
  currentPage +=1;

  $.ajax({
    type: 'GET',
    url: `/feeds/members/${memberId}` + param,
    success: function (response) {
      var feeds = response.content;

      if (!feeds.length) {
        alert('피드가 없습니다.');
      } else {
        var feedHtml = '';

        feeds.forEach(feed => {
          var feedImage = feed.feedImages.length !== 0 ? feed.feedImages[0].imagePath : '#';
          var memberImage = feed.member.image.imageStoreName;
          var memberNickname = feed.member.nickname;
          var feedPath = '/feed-page/' + feed.id;

          feedHtml += `
            <a href="${feedPath}">
              <div class="col-7 mb-4">
                <div class="card shadow-sm">
                  <img src="/images/${feedImage}" class="bd-placeholder-img card-img-top" width="100%" height="225" role="img" aria-label="Card Image" preserveAspectRatio="xMidYMid slice" focusable="false">
                  <div class="card-body">
                    <h5 class="card-title">${feed.title}</h5>
                    <p class="card-text">${feed.content}</p>
                    <div class="d-flex justify-content-between align-items-center">
                      <div>
                          <img src="/images/${memberImage}" alt="${memberNickname}" width="30" height="30">
                      </div>
                      <div class="ms-auto">
                        <div class="d-flex flex-column">
                          <small class="text-muted text-end mb-2">댓글(${feed.commentCount})</small>
                          <small class="text-mute text-end">${feed.createdAt}</small>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a>`;
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