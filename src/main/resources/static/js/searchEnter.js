function searchEnter(searchText) {
  console.log('searchEnter 호출');

  if (searchText !== null && searchText.length) {
    $.ajax({
      success: function () {
        location.href = `/?searchText=${searchText}`;
      }
    })
  }
}