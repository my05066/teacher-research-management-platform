


// 检查是否未快速录入
export const check_Item = (route, router, type1, openAddDialog) => {

  if (route.query.prefill === '1') {
    try {
      const raw = sessionStorage.getItem('quickEntryPrefill');
      if (raw) {
        const { type, data } = JSON.parse(raw);
        if (type === type1 && data) {
          sessionStorage.removeItem('quickEntryPrefill');
          router.replace({ path: `/${type1}`, query: {} });
          openAddDialog(data);
        }
      }
    } catch (e) {
      console.warn('解析快速录入预填数据失败', e);
    }
  }
}