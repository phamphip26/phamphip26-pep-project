package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account registerAccount(Account account) {
        return this.accountDAO.insertAccount(account);
    }

    public Account getAccountByUsername(String username) {
        return this.accountDAO.getAccountByUsername(username);
    }

    public Account loginAccount(String username, String password) {
        return this.accountDAO.getAccounLogin(username, password);
    }

    public Boolean checkAccountIdExist(int id) {
        return this.accountDAO.checkAccountExistById(id);
    }
}
