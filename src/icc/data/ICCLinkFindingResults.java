package icc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ICCLinkFindingResults
{
  public SymbolTable<VarInfo> varsST;
  public SymbolTable<IntentInfo> intentsST;
  public List<ICCLinkInfo<IntentInfo>> iccLinks;
  public IntentStats stats;

  public ICCLinkFindingResults()
  {
    this(true);
  }

  public ICCLinkFindingResults(boolean init)
  {
    if (init)
    {
      this.varsST = new SymbolTable<VarInfo>();
      this.intentsST = new SymbolTable<IntentInfo>();
      this.iccLinks = new ArrayList<ICCLinkInfo<IntentInfo>>();
      this.stats = new IntentStats();
    }
  }

  public void accessStats()
  {
    stats.iccLinks.value = iccLinks.size();
    stats.intentCount.value = intentsST.getMap().size() + getAnonymousIntentsCount();
    stats.explicitICCLinks.value = getExplicitLinksCount();
    stats.implicitICCLinks.value = getImplicitLinksCount();
    stats.explicitIntents.value = getExplicitIntentsCount();
    stats.implicitIntents.value = getImplicitIntentsCount();
  }

  private int getAnonymousIntentsCount()
  {
    int result = 0;

    for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks)
    {
      IntentInfo intentInfo = linkInfo.getTarget();

      // the intent is anonymous
      if (!intentsST.getMap().values().contains(intentInfo))
      {
        result++;
      }
    }

    return result;
  }

  private int getExplicitLinksCount()
  {
    int result = 0;

    for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks)
    {
      IntentInfo intentInfo = linkInfo.getTarget();

      // the intent is anonymous
      if (intentInfo.isExplicit())
      {
        result++;
      }
    }

    return result;
  }

  private int getImplicitLinksCount()
  {
    int result = 0;

    for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks)
    {
      IntentInfo intentInfo = linkInfo.getTarget();

      // the intent is anonymous
      if (!intentInfo.isExplicit())
      {
        result++;
      }
    }

    return result;
  }

  private int getExplicitIntentsCount()
  {
    int result = 0;

    // all named intents
    for (Map.Entry<String, IntentInfo> entry : this.intentsST.getMap().entrySet())
    {
      if (entry.getValue().isExplicit())
      {
        result++;
      }
    }

    // all anonymous intents
    for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks)
    {
      IntentInfo intentInfo = linkInfo.getTarget();

      if (!intentsST.getMap().values().contains(intentInfo))
      {
        if (intentInfo.isExplicit())
        {
          result++;
        }
      }
    }

    return result;
  }

  private int getImplicitIntentsCount()
  {
    int result = 0;

    // all named intents
    for (Map.Entry<String, IntentInfo> entry : this.intentsST.getMap().entrySet())
    {
      if (!entry.getValue().isExplicit())
      {
        result++;
      }
    }

    // all anonymous intents
    for (ICCLinkInfo<IntentInfo> linkInfo : iccLinks)
    {
      IntentInfo intentInfo = linkInfo.getTarget();

      if (!intentsST.getMap().values().contains(intentInfo))
      {
        if (!intentInfo.isExplicit())
        {
          result++;
        }
      }
    }

    return result;
  }
}
