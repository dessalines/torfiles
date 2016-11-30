import { TorsharePage } from './app.po';

describe('torshare App', function() {
  let page: TorsharePage;

  beforeEach(() => {
    page = new TorsharePage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
