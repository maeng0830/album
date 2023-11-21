let pageSize = 20;

function getFollow(currentPage, searchText, memberId) {
  console.log('getFollow 호출');
  getFollowings(currentPage, searchText, memberId);
  getFollowers(currentPage, searchText, memberId);
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

// 팔로우 끊기
function cancelFollow(followingId) {
  console.log('cancelFollow 호출');

  let url = `/api/follows/${followingId}`;

  $.ajax({
    type: 'DELETE',
    url: url,
    success: function (response) {
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert(response.message);
      }
    },
    error: function () {
      alert("팔로우를 취소를 실패했습니다.");
    }
  })
}

// 팔로잉 목록
function getFollowings(currentPage, searchText, followerId) {
  console.log('getFollowing 호출');

  let url = `/api/follows/following/${followerId}?`;

  if (searchText !== null && searchText.length) {
    url += `searchText=${searchText}&`;
  }

  url += `page=${currentPage}&size=${pageSize}`;

  $.ajax({
    type: 'GET',
    url: url,
    success: function (response) {
      var totalPage = response.totalPages;

      if (response.code && response.message) {
        alert(response.message);
      } else if (!response.content.length) {
        alert('불러올 팔로잉 목록이 없습니다.')
      } else {
        var followingHtml = `<div id="following-list">`;
        var follows = response.content;
        follows.forEach(follow => {
          var followingId = follow.following.id;
          var followingNickname = follow.following.nickname;

          followingHtml += `<div class="row row-cols-12 justify-content-center text-center">
            <div class="col-3 align-self-center">
              ${followingNickname}
            </div>
            <div class="col-3">
              <button
                  class="changeStatusBtn btn btn-outline-primary btn-sm"
                  onclick="showFollowList(${followingId}, 0, 'getFollowers')">팔로워 보기
              </button>
            </div>
            <div class="col-3">
              <button
                  class="changeStatusBtn btn btn-outline-primary btn-sm"
                  onclick="showFollowList(${followingId}, 0, 'getFollowings')">팔로잉 보기
              </button>
            </div>
            <div class="col-3">
              <button
                  class="changeStatusBtn btn btn-outline-primary btn-sm"
                  onclick="cancelFollow(${followingId})">팔로우 끊기
              </button>
            </div>
          </div>`;
        })

        followingHtml += `</div>`;

        $("#following-list").html(followingHtml);

        var paginationHtml = pageLink(followerId, currentPage, totalPage, searchText, "getFollowings");

        $('#following-list-pagination').html(paginationHtml);
      }
    },
    error: function () {
      alert("팔로잉 목록을 불러오는데 실패했습니다.");
    }
  })
}

// 팔로워 목록
function getFollowers(currentPage, searchText, followingId) {
  console.log('getFollowers 호출');

  let url = `/api/follows/follower/${followingId}?`;

  if (searchText !== null && searchText.length) {
    url += `searchText=${searchText}&`;
  }

  url += `page=${currentPage}&size=${pageSize}`;

  $.ajax({
    type: 'GET',
    url: url,
    success: function (response) {
      var totalPage = response.totalPages;

      if (response.code && response.message) {
        alert(response.message);
      } else if (!response.content.length) {
        alert('불러올 팔로우 목록이 없습니다.')
      } else {
        var followerHtml = `<div id="follower-list">`;
        var follows = response.content;
        follows.forEach(follow => {
          var followerId = follow.follower.id;
          var followerNickname = follow.follower.nickname;

          followerHtml += `<div class="row row-cols-12 justify-content-center text-center">
            <div class="col-3 align-self-center">
              ${followerNickname}
            </div>
            <div class="col-3">
              <button
                  class="changeStatusBtn btn btn-outline-primary btn-sm"
                  onclick="showFollowList(${followerId}, 0, 'getFollowers')">팔로워 보기
              </button>
            </div>
            <div class="col-3">
              <button
                  class="changeStatusBtn btn btn-outline-primary btn-sm"
                  onclick="showFollowList(${followerId}, 0, 'getFollowings')">팔로잉 보기
              </button>
            </div>
            <div class="col-3">
              <button
                  class="changeStatusBtn btn btn-outline-primary btn-sm"
                  onclick="follow(${followerId})">팔로우 하기
              </button>
            </div>
          </div>`;
        })

        followerHtml += `</div>`;

        $("#follower-list").html(followerHtml);

        var paginationHtml = pageLink(followingId, currentPage, totalPage, searchText, "getFollowers");

        $('#follower-list-pagination').html(paginationHtml);
      }
    },
    error: function () {
      alert("팔로워 목록을 불러오는데 실패했습니다.");
    }
  })
}

// 페이징
function pageLink(memberId, currentPage, totalPage, searchText, funcName) {
  var pageUrl = `<nav id=\"following-list-pagination\" aria-label=\"Page navigation example\">
                  <ul class="pagination justify-content-center">`;

  var pageLimit = 5;
  var startPage = parseInt((currentPage - 1) / pageSize) * pageSize;
  var endPage = startPage + pageLimit;

  if (totalPage < endPage) {
    endPage = totalPage;
  }

  var nextPage = endPage + 1;

  // 맨 첫 페이지
  if (currentPage > 1 && pageLimit < currentPage) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(0, ${searchText}, ${memberId})"><span>«</span></a></li>`;
  }

  // 이전 페이지
  if (currentPage > pageLimit) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${startPage == 1 ? 1 : startPage - 1}, ${searchText}, ${memberId})"><span>이전</span></a></li>`;
  }

  // pageLimit에 맞게 페이지를 보여줌
  for (var i = startPage; i < endPage; i++) {
    if (i === currentPage) {
      pageUrl += `<li class="page-item active"><a class="page-link" href="#">${i + 1}</a></li>`;
    } else {
      pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${i}, ${searchText}, ${memberId})">${i + 1}</a></li>`;
    }
  }

  // 다음 페이지
  if (nextPage <= totalPage) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${nextPage < totalPage ? nextPage : totalPage}, ${searchText}, ${memberId})"><span>다음</span></a></li>`;
  }

  // 마지막 페이지
  if (currentPage < totalPage && nextPage < totalPage) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${totalPage}, ${searchText}, ${memberId})"><span>»</span></a></li>`;
  }

  pageUrl += `</ul></nav>`;

  return pageUrl;
}

// 팔로우 리스트
function showFollowList(memberId, currentPage, funcName) {
  if (funcName === 'getFollowings') {
    var urlForFollowing = `/api/follows/following/${memberId}?page=0&size=${pageSize}`
    var followingListHtml = ``;

    $.ajax({
      type: 'GET',
      url: urlForFollowing,
      success: function (response) {
        if (response.code && response.message) {
          alert(response.message);
        } else if (!response.content.length) {
          alert("불러올 팔로잉 목록이 없습니다.")
        } else {
          var totalPage = response.totalPages;
          var follows = response.content;
          follows.forEach(follow => {
            var followingId = follow.following.id;
            var followingNickname = follow.following.nickname;

            followingListHtml += `<div class="row row-cols-4 justify-content-md-center text-center">
                                  <div class="col-2 align-self-center">${followingNickname}</div>
                                  <div class="col-2">
                                    <button
                                      class="changeStatusBtn btn btn-outline-primary btn-sm"
                                      onclick="follow(${followingId})">팔로우 하기
                                    </button>
                                  </div>
                                </div>`;
          })

          $('#follow-list').html(followingListHtml);
          $('#follow-list-pagination').html(pageLink(memberId, currentPage, totalPage, null, funcName));
          $('#followListTemplate').modal('show');
        }
      },
      error: function () {
        alert("팔로잉 목록을 불러오는데 실패했습니다.");
      }
    })
  } else if (funcName === 'getFollowers') {
      var urlForFollower = `/api/follows/follower/${memberId}?page=0&size=${pageSize}`;
      var followerListHtml = ``;

      $.ajax({
        type: 'GET',
        url: urlForFollower,
        success: function (response) {
          if (response.code && response.message) {
            alert(response.message);
          } else if (!response.content.length) {
            alert("불러올 팔로워 목록이 없습니다.")
          } else {
            var totalPage = response.totalPages;
            var follows = response.content;
            follows.forEach(follow => {
              var followerId = follow.follower.id;
              var followerNickname = follow.follower.nickname;

              followerListHtml += `<div class="row row-cols-4 justify-content-md-center text-center">
                                    <div class="col-2 align-self-center">${followerNickname}</div>
                                    <div class="col-2">
                                      <button
                                        class="changeStatusBtn btn btn-outline-primary btn-sm"
                                        onclick="follow(${followerId})">팔로우 하기
                                      </button>
                                    </div>
                                  </div>`;
            })

            $('#follow-list').html(followerListHtml);
            $('#follow-list-pagination').html(pageLink(memberId, currentPage, totalPage, null, funcName));
            $('#followListTemplate').modal('show');
          }
        },
        error: function () {
          alert("팔로워 목록을 불러오는데 실패했습니다.");
        }
      })
  }
}