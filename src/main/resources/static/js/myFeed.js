let currentPage = 0;
let pageSize = 3;

function getMyFeeds(memberId) {

  let param = `?page=${currentPage}&size=${pageSize}`;
  currentPage +=1;

  $.ajax({
    type: 'GET',
    url: `/api/feeds/members/${memberId}` + param,
    success: function (response) {
      var feeds = response.content;

      if (!feeds.length) {
        alert('피드가 없습니다.');
      } else {
        var feedHtml = '';

        feeds.forEach(feed => {
          var feedImage = feed.feedImages.length !== 0 ? feed.feedImages[0].imageStoreName : '#';
          var memberId = feed.member.id;
          var memberImage = feed.member.image.imageStoreName;
          var memberNickname = feed.member.nickname;
          var feedPath = '/feed-page/' + feed.id;

          feedHtml += `
              <div class="col-7 mb-4">
                <a href="${feedPath}">
                  <div class="card shadow-sm">
                    <img src="/images/${feedImage}" class="bd-placeholder-img card-img-top" width="100%" height="225" role="img" aria-label="Card Image" preserveAspectRatio="xMidYMid slice" focusable="false">
                    <div class="card-body">
                      <h5 class="card-title">${feed.title}</h5>
                      <p class="card-text">${feed.content}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <div>
                          <a class="nav-link px-2 text-white dropdown-toggle" role="button" id="memberDropdown_${memberId}" data-bs-toggle="dropdown" aria-expanded="false" href="#">
                           <img src="/images/${memberImage}" alt="${memberNickname}" width="30" height="30">
                          </a>
                          <ul class="dropdown-menu" aria-labelledby="memberDropdown_${memberId}">
                            <li><p class="dropdown-item">${memberNickname}</p></li>
                            <li><a class="dropdown-item" onclick="follow(${memberId})">팔로우 하기</a></li>
                          </ul>
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
                </a>
              </div>`;
        })

        if (currentPage === 0) {
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

// 팔로우 하기
function follow(followingId) {
  console.log('follow 호출');

  let url = `/api/follows/${followingId}`;

  $.ajax({
    type: 'POST',
    url: url,
    success: function (response) {
      if (response.code && response.message) {
        alert(response.message);
      } else {
        var follower = response.follower.nickname;
        var following = response.following.nickname;

        alert(`${follower}님이 ${following}님을 팔로우 했습니다.`);
      }
    },
    error: function () {
      alert("팔로우를 실패했습니다.");
    }
  })
}