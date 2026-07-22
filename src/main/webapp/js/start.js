function confirmReset() {
    if (confirm("初期化すると現在の状態がすべてリセットされます。よろしいですか？")) {
        document.getElementById("resetForm").submit();
    }
}