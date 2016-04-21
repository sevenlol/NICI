package tw.gov.ey.nici.events;

import tw.gov.ey.nici.models.NiciIntro;

public class IntroDataReadyEvent {
    private NiciIntro intro;

    public IntroDataReadyEvent(NiciIntro intro) {
        if (intro == null || intro.getContentList() == null) {
            throw new IllegalArgumentException();
        }
        this.intro = intro;
    }

    public NiciIntro getIntro() { return intro; }
}
