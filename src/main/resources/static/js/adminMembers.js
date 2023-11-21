let pageSize = 20;
let currentPageForChangeStatus = 0; // 상태 변경 후, 페이지 리로드를 위한 변수
let searchTextForChangeStatus = null; // 상태 변경 후, 페이지 리로드를 위한 변수

// 목록 가져오기
function getAdminMembers(currentPage, searchText) {
  console.log('getAdminMembers 호출');
  let url = '/api/admin/members' + '?page=' + currentPage + '&size=' + pageSize;

  if (searchText != null) {
    console.log("searchText Not Null. add searchText Param.");
    url += '&searchText=' + searchText;
  }

  $.ajax({
    type: 'GET',
    url: url,
    success: function (responses) {
      var members = responses.content;
      var statusNormal = 'NORMAL';
      var statusLocked = 'LOCKED';
      var statusWithdraw = 'WITHDRAW';
      var totalPage = responses.totalPages;

      if (!members.length) {
        alert('불러올 목록이 없습니다.');
      } else {
        var memberHtml = '<div id="member-list" class="container">';

        members.forEach(m => {
          var memberId = m.id;
          var memberUsername = m.username;
          var memberNickname = m.nickname;
          var memberCreatedAt = m.createdAt;
          var memberRole = m.role;
          var memberStatus = m.status;

          memberHtml += `<div class="row row-cols-11 justify-content-md-center text-center">
                            <div class="col-1">${memberId}</div>
                            <div class="col-2">${memberUsername}</div>
                            <div class="col-2">${memberNickname}</div>
                            <div class="col-2">${memberCreatedAt}</div>
                            <div class="col-1">${memberRole}</div>
                            <div class="col-1">${memberStatus}</div>
                            <div class="col-2">
                              <div class="row">
                                <button 
                                  class="changeStatusBtn btn btn-outline-primary btn-sm" 
                                  onclick="changeStatus(${memberId}, '${statusNormal}')">Normal</button>
                              </div>
                              <div class="row">
                                <button 
                                  class="changeStatusBtn btn btn-outline-primary btn-sm" 
                                  onclick="changeStatus(${memberId}, '${statusLocked}')">Locked</button>
                              </div>
                              <div class="row">
                                <button 
                                  class="changeStatusBtn btn btn-outline-primary btn-sm" 
                                  onclick="changeStatus(${memberId}, '${statusWithdraw}')">Withdraw</button>
                              </div>
                            </div>
                        </div>
                        <hr class="my-4">`;
        })

        memberHtml += '</div>';

        $("#member-list").html(memberHtml);

        var paginationHtml = pageLink(currentPage, totalPage, searchText, "getAdminMembers");

        $('#pagination').html(paginationHtml);

        currentPageForChangeStatus = currentPage;
        searchTextForChangeStatus = searchText;
      }
    },
    error: function () {
      alert("회원 목록을 가져오는데 실패했습니다.");
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
function changeStatus(memberId, status) {
  console.log('changeStatus 호출');

  var url = `/api/admin/members/status`;

  var memberStatus = {
    id: memberId,
    memberStatus: status
  }

  $.ajax({
    type: 'PUT',
    url: url,
    data: JSON.stringify(memberStatus),
    contentType: 'application/json',
    success: function (response) {
      // 응답 데이터가 AlbumException 타입인지 확인
      if (response.code && response.message) {
        alert(response.message);
      } else {
        alert("회원 상태가 변경되었습니다.");
        getAdminMembers(currentPageForChangeStatus, searchTextForChangeStatus);
      }
    },
    error: function () {
      alert("회원 상태를 변경하는데 실패했습니다.");
    }
  })
}