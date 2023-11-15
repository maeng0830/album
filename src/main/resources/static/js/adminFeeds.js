let pageSize = 20;
let currentPageForChangeStatus = 0; // 상태 변경 후, 페이지 리로드를 위한 변수
let searchTextForChangeStatus = null; // 상태 변경 후, 페이지 리로드를 위한 변수

// 목록 가져오기
function getAdminFeeds(currentPage, searchText) {
  console.log('getFeeds 호출');
  let url = '/admin/feeds' + '?page=' + currentPage + '&size=' + pageSize;

  if (searchText != null) {
    console.log("searchText Not Null. add searchText Param.");
    url += '&searchText=' + searchText;
  }

  $.ajax({
    type: 'GET',
    url: url,
    success: function (responses) {
      var feeds = responses.content;
      var statusNormal = 'NORMAL';
      var statusAccuse = 'ACCUSE';
      var statusDelete = 'DELETE';
      var totalPage = responses.totalPages;

      if (!feeds.length) {
        alert('불러올 목록이 없습니다.');
      } else {
        var feedHtml = '<div id="feed-list" class="container">';

        feeds.forEach(f => {
          var feedId = f.id;
          var writerUsername = f.member.username;
          var writerNickname = f.member.nickname;
          var feedTitle = f.title;
          var feedCreatedAt = f.createdAt;
          var feedStatus = f.status;

          feedHtml += `<div class="row row-cols-11 justify-content-md-center text-center">
                            <div class="col-1">${feedId}</div>
                            <div class="col-2">${writerUsername}</div>
                            <div class="col-2">${writerNickname}</div>
                            <div class="col-2">${feedTitle}</div>
                            <div class="col-1">${feedCreatedAt}</div>
                            <div class="col-1">${feedStatus}</div>
                            <div class="col-1">
                              <button 
                                  class="accusesBtn btn btn-outline-primary btn-sm"
                                  onclick="showAccuses(${feedId})" 
                                  id="accusesBtn">신고 내역</button>
                            </div>
                            <div class="col-1">
                              <div class="row">
                                <button 
                                  class="changeStatusBtn btn btn-outline-primary btn-sm" 
                                  onclick="changeStatus(${feedId}, '${statusNormal}')">Normal</button>
                              </div>
                              <div class="row">
                                <button 
                                  class="changeStatusBtn btn btn-outline-primary btn-sm" 
                                  onclick="changeStatus(${feedId}, '${statusAccuse}')">Accuse</button>
                              </div>
                              <div class="row">
                                <button 
                                  class="changeStatusBtn btn btn-outline-primary btn-sm" 
                                  onclick="changeStatus(${feedId}, '${statusDelete}')">Delete</button>
                              </div>
                            </div>
                        </div>
                        <hr class="my-4">`;
        })

        feedHtml += '</div>';

        $("#feed-list").html(feedHtml);

        var paginationHtml = pageLink(currentPage, totalPage, searchText, "getAdminFeeds");

        $('#pagination').html(paginationHtml);

        currentPageForChangeStatus = currentPage;
        searchTextForChangeStatus = searchText;
      }
    },
    error: function () {
      alert("피드 목록을 가져오는데 실패했습니다.");
    }
  })
}

// 페이징
function pageLink(currentPage, totalPage, searchText, funcName) {
  var pageUrl = `<nav id=\"pagination\" aria-label=\"Page navigation example\">
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
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(0, ${searchText})"><span>«</span></a></li>`;
  }

  // 이전 페이지
  if (currentPage > pageLimit) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${startPage == 1 ? 1 : startPage - 1}, ${searchText})"><span>이전</span></a></li>`;
  }

  // pageLimit에 맞게 페이지를 보여줌
  for (var i = startPage; i < endPage; i++) {
    if (i === currentPage) {
      pageUrl += `<li class="page-item active"><a class="page-link" href="#">${i + 1}</a></li>`;
    } else {
      pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${i}, ${searchText})">${i + 1}</a></li>`;
    }
  }

  // 다음 페이지
  if (nextPage <= totalPage) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${nextPage < totalPage ? nextPage : totalPage}, ${searchText})"><span>다음</span></a></li>`;
  }

  // 마지막 페이지
  if (currentPage < totalPage && nextPage < totalPage) {
    pageUrl += `<li class="page-item"><a class="page-link" href="javascript: ${funcName}(${totalPage}, ${searchText})"><span>»</span></a></li>`;
  }

  pageUrl += `</ul></nav>`;

  return pageUrl;
}

// 상태 변경
function changeStatus(feedId, status) {
  console.log('changeStatus 호출');

  var url = `/admin/feeds/status`;

  var feedStatus = {
    id: feedId,
    feedStatus: status
  }

  $.ajax({
    type: 'PUT',
    url: url,
    data: JSON.stringify(feedStatus),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("피드 상태가 변경되었습니다.");
        getAdminFeeds(currentPageForChangeStatus, searchTextForChangeStatus);
      }
    },
    error: function () {
      alert("피드 상태를 변경하는데 실패했습니다.");
    }
  })
}

// 신고 내역
function showAccuses(feedId) {
  console.log('showAccuses 호출');

  var url = `/admin/feeds/${feedId}/accuses`;

  $.ajax({
    type: 'GET',
    url: url,
    success: function (response) {
      if (!response.length) {
        alert('신고 내역이 없습니다.');
        return false;
      } else {
        var accusesHtml = ``;

        response.forEach(a => {
          var accusedFeedId = a.feedId;
          var content = a.content;
          var memberUsername = a.username;
          var memberNickname = a.nickname;

          accusesHtml += `<div class="row row-cols-9 justify-content-md-center text-center">
                          <div class="col-1">${accusedFeedId}</div>
                          <div class="col-2">${memberUsername}</div>
                          <div class="col-2">${memberNickname}</div>
                          <div class="col-4">${content}</div>
                        </div>`;
        })

        $('#accuseReason-list').html(accusesHtml);
        $('#modalAccuses').modal('show');
      }
    }
  })
}