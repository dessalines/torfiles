import { PeerHealthPipe } from './peer-health.pipe';

describe('PeerHealthPipe', () => {
  it('create an instance', () => {
    const pipe = new PeerHealthPipe();
    expect(pipe).toBeTruthy();
  });
});
