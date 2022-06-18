package br.ce.astrid.api.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import br.ce.astrid.api.test.Account;
import br.ce.astrid.api.test.Board;
import br.ce.astrid.api.test.Login;
import br.ce.astrid.api.test.Stage;
import br.ce.astrid.api.test.Task;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
    Account.class,
    Login.class,
    Board.class,
    Stage.class,
    Task.class
})
public class Suite {}
